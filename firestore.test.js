const {
  initializeTestEnvironment,
  assertFails,
  assertSucceeds,
} = require("@firebase/rules-unit-testing");
const { test, before, after, beforeEach } = require("node:test");
const fs = require("node:fs");

let testEnv;
const PROJECT_ID = process.env.GCP_PROJECT || "demo-no-project";
const ALICE_UID = "alice_tr_123";
const BOB_UID = "bob_tr_456";

const [emulatorHost, emulatorPortStr] = (process.env.FIRESTORE_EMULATOR_HOST || "127.0.0.1:8085").split(":");
const emulatorPort = parseInt(emulatorPortStr, 10);

before(async () => {
  const rules = fs.readFileSync("./firestore.rules", "utf8");
  testEnv = await initializeTestEnvironment({
    projectId: PROJECT_ID,
    firestore: {
      rules,
      host: emulatorHost,
      port: emulatorPort,
    },
  });
});

after(async () => {
  if (testEnv) {
    await testEnv.cleanup();
  }
});

beforeEach(async () => {
  if (testEnv) {
    await testEnv.clearFirestore();
  }
});

const pastTime = () => new Date(Date.now() - 2000);
const futureTime = () => new Date(Date.now() + 60000);

test("Unauthenticated users cannot read or write any collection", async () => {
  const unauthDb = testEnv.unauthenticatedContext().firestore();
  await assertFails(unauthDb.collection("users").doc(ALICE_UID).get());
  await assertFails(unauthDb.collection("users").doc(ALICE_UID).collection("enquiries").get());
  await assertFails(
    unauthDb.collection("users").doc(ALICE_UID).collection("enquiries").doc("enq_1").set({
      name: "Alice",
      email: "alice@example.com",
      serviceType: "Website development",
      message: "Need a site",
      status: "submitted",
      createdAt: pastTime(),
      updatedAt: pastTime(),
    })
  );
});

test("UserProfile create, update, and privilege escalation prevention", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const bobDb = testEnv.authenticatedContext(BOB_UID).firestore();
  const ts = pastTime();

  // Valid create matching Kotlin UserProfile.toCreateMap()
  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).set({
      userId: ALICE_UID,
      displayName: "Alice Founder",
      email: "alice@example.com",
      businessName: "Alice Labs",
      clientStatus: "visitor",
      tier: "Starter",
      bonds: 0,
      loyaltyPoints: 0,
      createdAt: ts,
      updatedAt: ts,
    })
  );

  // Valid partial update of businessName
  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).update({
      businessName: "Alice Global Co.",
      updatedAt: new Date(Date.now() - 1000),
    })
  );

  // Reject self-escalation of loyaltyPoints or clientStatus
  await assertFails(
    aliceDb.collection("users").doc(ALICE_UID).update({
      loyaltyPoints: 9999,
      updatedAt: new Date(Date.now() - 500),
    })
  );
  await assertFails(
    aliceDb.collection("users").doc(ALICE_UID).update({
      clientStatus: "active",
      updatedAt: new Date(Date.now() - 500),
    })
  );

  // Reject Ghost Field on create
  await assertFails(
    bobDb.collection("users").doc(BOB_UID).set({
      userId: BOB_UID,
      displayName: "Bob",
      email: "bob@example.com",
      clientStatus: "visitor",
      tier: "Starter",
      bonds: 0,
      loyaltyPoints: 0,
      isAdmin: true,
      createdAt: ts,
      updatedAt: ts,
    })
  );

  // Reject cross-user read and unfiltered list
  await assertFails(bobDb.collection("users").doc(ALICE_UID).get());
  await assertFails(aliceDb.collection("users").get());
});

test("Enquiries: Kotlin payload create, partial archive, reject @DocumentId or future timestamp", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const bobDb = testEnv.authenticatedContext(BOB_UID).firestore();
  const ts = pastTime();

  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).collection("enquiries").doc("enq_1").set({
      name: "Alice Founder",
      email: "alice@example.com",
      businessName: "Alice Labs",
      serviceType: "Website development",
      plan: "Grow",
      budget: "500–1,500 USD",
      message: "Looking for a multi-page company website.",
      status: "submitted",
      createdAt: ts,
      updatedAt: ts,
    })
  );

  // List own enquiries succeeds
  await assertSucceeds(aliceDb.collection("users").doc(ALICE_UID).collection("enquiries").get());

  // Cross-user list/get/write fails
  await assertFails(bobDb.collection("users").doc(ALICE_UID).collection("enquiries").get());
  await assertFails(bobDb.collection("users").doc(ALICE_UID).collection("enquiries").doc("enq_1").get());

  // Partial update to archive succeeds, subsequent edit after terminal archived fails
  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).collection("enquiries").doc("enq_1").update({
      status: "archived",
      updatedAt: new Date(Date.now() - 1000),
    })
  );
  await assertFails(
    aliceDb.collection("users").doc(ALICE_UID).collection("enquiries").doc("enq_1").update({
      message: "Changed after archive",
      updatedAt: new Date(Date.now() - 500),
    })
  );

  // Reject DocumentId leakage (`id`) and future timestamp
  await assertFails(
    aliceDb.collection("users").doc(ALICE_UID).collection("enquiries").doc("enq_bad_id").set({
      id: "enq_bad_id",
      name: "Alice",
      email: "alice@example.com",
      serviceType: "Website development",
      message: "Has extra id key",
      status: "submitted",
      createdAt: ts,
      updatedAt: ts,
    })
  );
  await assertFails(
    aliceDb.collection("users").doc(ALICE_UID).collection("enquiries").doc("enq_future").set({
      name: "Alice",
      email: "alice@example.com",
      serviceType: "Website development",
      message: "Tampered future timestamp",
      status: "submitted",
      createdAt: futureTime(),
      updatedAt: futureTime(),
    })
  );
});

test("Applications, Access Requests, Support Tickets, and Portal Messages", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const bobDb = testEnv.authenticatedContext(BOB_UID).firestore();
  const ts = pastTime();

  // Application
  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).collection("applications").doc("app_1").set({
      name: "Alice",
      email: "alice@example.com",
      role: "Frontend developer",
      portfolioUrl: "https://alice.dev",
      message: "Experienced in responsive design.",
      status: "received",
      createdAt: ts,
      updatedAt: ts,
    })
  );

  // Access request
  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).collection("access_requests").doc("req_1").set({
      name: "Alice",
      email: "alice@example.com",
      businessName: "Alice Labs",
      dealDetails: "Grow plan agreed on 18 Nov.",
      status: "pending_review",
      createdAt: ts,
      updatedAt: ts,
    })
  );

  // Reject self-approving an access request
  await assertFails(
    aliceDb.collection("users").doc(ALICE_UID).collection("access_requests").doc("req_2").set({
      name: "Alice",
      email: "alice@example.com",
      dealDetails: "Trying to self-approve",
      status: "approved",
      createdAt: ts,
      updatedAt: ts,
    })
  );

  // Support ticket create & close
  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).collection("support_tickets").doc("sup_1").set({
      category: "Website issue",
      priority: "Normal",
      projectRef: "TR-C-000123",
      message: "Header link alignment on mobile.",
      status: "open",
      createdAt: ts,
      updatedAt: ts,
    })
  );
  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).collection("support_tickets").doc("sup_1").update({
      status: "closed",
      updatedAt: new Date(Date.now() - 500),
    })
  );
  // Cannot reopen closed ticket
  await assertFails(
    aliceDb.collection("users").doc(ALICE_UID).collection("support_tickets").doc("sup_1").update({
      status: "open",
      updatedAt: new Date(Date.now() - 200),
    })
  );

  // Portal message
  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).collection("portal_messages").doc("msg_1").set({
      body: "Hello team, please confirm domain DNS setup.",
      sender: "client",
      createdAt: ts,
      updatedAt: ts,
    })
  );
  await assertFails(
    bobDb.collection("users").doc(ALICE_UID).collection("portal_messages").get()
  );
});
