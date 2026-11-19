# TRUE RYZE Portal: architecture notes (for connecting the backend)

## Status
The front end is complete: Account page (request client access), Portal landing (Client on top, Team, Dealer), and three dashboards.
Dashboards are PREVIEWS with labelled sample data (see `SAMPLE` in the script). Sign-in, accounts, approvals and live data need a backend + auth (not connected).

## Business flow
Visitor explores -> Contact form -> deal done -> "Request client access" (Account page) -> Dealer/admin approves
-> unique permanent Client ID (TR-C-######) -> client creates a Portal password (or Google) using that ID -> Client Portal monitor.
Team and clients never connect directly: work goes Team -> TRUE RYZE (dealer) -> Client. They are linked only by IDs.

## Roles and ID formats
| Role | ID | Created by | Sees |
|---|---|---|---|
| Client | TR-C-###### | issued on approval | own order, team ID only, services, pricing, loyalty, bonds, growth, AI |
| Team | TR-T-#### | TRUE RYZE (no sign-up) | assigned works, Client IDs only, deadlines, pay, delivery upload |
| Dealer | TR-D-### + access key | TRUE RYZE only | everything: requests, assignments, deadlines, status, teams, clients |

## Suggested data model (Postgres / Supabase)
clients(id, client_code, business, email, tier, bonds, points) · access_requests(id, name, email, business, deal_details, status, approved_by, client_code)
teams(id, team_code, pay_rate) · orders(id, client_id, service, stage, deadline, team_id, progress)
works(id, order_id, team_id, task, deadline, status, pay) · loyalty_ledger(id, client_id, delta, reason, at)
services(id, client_id, kind[past|monthly], name, renews_at) · payments(id, client_id, label, amount_minor, currency)
messages/support(id, client_id, body, at) · audit_log(...)

## Security rules (must be enforced server-side, never in the browser)
* Row-level security: a client reads only rows with their client_id; a team member only works assigned to them, with client_code but no client PII; dealer reads all.
* Password hashing + rate limiting + lockout; dealer needs a second factor (the access key is a placeholder for this).
* Client ID approval is an admin-only server action; IDs are random-unique, never reused.
* Loyalty redemption (points -> 1 free month) is a server transaction writing a ledger row.
* AI assistant runs server-side with the user's own rows as context; API key never in the page.

## Where the code hooks in
`CONFIG.formEndpoint` (forms) · `WHATSAPP`, `PROFILES` (links) · `portalForms()` (replace the "not connected" messages with real auth calls)
· `SAMPLE` (replace with API data) · `PORTAL_STATUS` (remove the preview notice when live).
