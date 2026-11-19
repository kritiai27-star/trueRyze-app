package com.example.data.model

data class LiveServiceSpec(
    val id: String,
    val number: String,
    val title: String,
    val subtitle: String,
    val summary: String,
    val highlights: List<String>,
    val included: List<String>,
    val whoItsFor: String,
    val whatWeNeed: List<String>,
    val notIncluded: List<String>,
    val steps: List<Pair<String, String>>,
    val notes: List<String>
)

data class ComingSoonServiceSpec(
    val id: String,
    val title: String,
    val description: String
)

data class PricingPlanSpec(
    val name: String,
    val audience: String,
    val priceLabel: String,
    val isHighlighted: Boolean,
    val items: List<String>,
    val note: String
)

data class PlanComparisonRow(
    val feature: String,
    val launchIncluded: Boolean,
    val growIncluded: Boolean,
    val scaleIncluded: Boolean
)

data class ProcessPhaseSpec(
    val phaseLabel: String,
    val title: String,
    val steps: List<ProcessStepSpec>
)

data class ProcessStepSpec(
    val number: Int,
    val title: String,
    val description: String,
    val isOptional: Boolean = false
)

data class FaqItem(
    val question: String,
    val answer: String
)

data class LifecycleStageSpec(
    val index: Int,
    val numberLabel: String,
    val title: String,
    val phase: String,
    val description: String,
    val isOptional: Boolean = false
)

object TrueRyzeCatalog {

    val quotes = listOf(
        "\"Great businesses are never built alone.\"",
        "\"Your rise is the reason we exist.\"",
        "\"Bring the vision. We'll build the way up.\""
    )

    val challenges = listOf(
        Triple(
            "Not online yet",
            "Customers search before they visit. A clear website makes your business findable and credible.",
            "store"
        ),
        Triple(
            "Site feels outdated",
            "Slow, broken or confusing pages cost trust. We modernise and maintain them.",
            "layers"
        ),
        Triple(
            "No time for tech",
            "You run the business. We handle building, updates and technical support.",
            "clock"
        )
    )

    val liveServices = listOf(
        LiveServiceSpec(
            id = "website-development",
            number = "01",
            title = "Website development",
            subtitle = "Websites designed around your business goals.",
            summary = "Business sites, startup sites and landing pages, designed around your goals and built to work on every device.",
            highlights = listOf(
                "Responsive design",
                "Business-focused structure",
                "Launch and handover"
            ),
            included = listOf(
                "Business and startup websites",
                "Professional landing pages",
                "Custom website experiences",
                "E-commerce, only where scope and requirements allow it",
                "Basic SEO and performance foundations"
            ),
            whoItsFor = "Startups, retailers, local businesses and established companies that need a professional website.",
            whatWeNeed = listOf(
                "Basic details about your business and goals",
                "Your content, text and images, or agreement on who prepares them",
                "Timely feedback during review rounds",
                "Access to your domain and hosting if relevant"
            ),
            notIncluded = listOf(
                "Digital marketing, lead generation and automation (coming soon)",
                "Ranking, traffic or sales guarantees",
                "Anything not listed in the scope agreed in writing"
            ),
            steps = listOf(
                "Discovery" to "We learn your business, audience and goals before designing.",
                "Design and build" to "Responsive pages with a clear structure and conversion-minded layout.",
                "Review" to "You review progress and request changes in agreed rounds.",
                "Quality checks" to "Testing across devices and browsers before launch.",
                "Launch and handover" to "Your site goes live and we hand it over to you."
            ),
            notes = listOf(
                "Final deliverables, timelines and pricing are confirmed in writing before work starts."
            )
        ),
        LiveServiceSpec(
            id = "website-maintenance",
            number = "02",
            title = "Website maintenance & support",
            subtitle = "Keep your website healthy after launch.",
            summary = "Updates, fixes, performance and security upkeep, plus technical help when you need it.",
            highlights = listOf(
                "Updates and bug fixing",
                "Technical and security upkeep",
                "Ongoing technical assistance"
            ),
            included = listOf(
                "Website and content updates",
                "Bug fixing",
                "Technical and security maintenance",
                "Performance improvements",
                "Ongoing technical assistance"
            ),
            whoItsFor = "Businesses with a live website that need updates, fixes and ongoing technical help.",
            whatWeNeed = listOf(
                "Access to the website and hosting",
                "A clear description of each change or issue",
                "A named contact for approvals"
            ),
            notIncluded = listOf(
                "Redesigns and new features, which are scoped as separate projects",
                "Marketing, lead generation and automation (coming soon)",
                "Guaranteed response or uptime times, until a support process is formally defined"
            ),
            steps = listOf(
                "Updates" to "Content and website changes as agreed.",
                "Fixes" to "Bug fixing when something breaks.",
                "Care" to "Technical and security maintenance.",
                "Speed" to "Performance improvements.",
                "Help" to "Technical assistance when you need it."
            ),
            notes = listOf(
                "Maintenance is a separate, recurring arrangement from development. Response times are not guaranteed until a support process is formally defined."
            )
        )
    )

    val comingSoonServices = listOf(
        ComingSoonServiceSpec("ai", "AI Business Automation", "Automating repetitive business work."),
        ComingSoonServiceSpec("mkt", "Digital Marketing", "Reach the right audience online."),
        ComingSoonServiceSpec("lead", "Lead Generation", "Turn attention into enquiries."),
        ComingSoonServiceSpec("grow", "Business Growth & Scaling", "Structured support to scale up."),
        ComingSoonServiceSpec("res", "Startup Resources", "Strategies and execution for founders."),
        ComingSoonServiceSpec("more", "More Digital Solutions", "Further digitisation services."),
        ComingSoonServiceSpec("seo", "SEO & Search Visibility", "Help the right customers find you on search."),
        ComingSoonServiceSpec("brand", "Brand Identity & Design", "A look and feel that is unmistakably yours."),
        ComingSoonServiceSpec("stats", "Analytics & Reporting", "Clear numbers on how your website performs.")
    )

    val deliveryGuarantees = listOf(
        "Plain-English scoping, no jargon",
        "Scope, deliverables and pricing confirmed before work starts",
        "Review rounds so you shape the result",
        "Testing and quality checks before launch",
        "Optional ongoing maintenance after handover"
    )

    val plans = listOf(
        PricingPlanSpec(
            name = "Launch",
            audience = "Local shops, solo founders, first websites",
            priceLabel = "Price to be confirmed",
            isHighlighted = false,
            items = listOf(
                "Professional landing page or small business site",
                "Responsive design for all devices",
                "Contact form and basic SEO setup",
                "Launch and handover"
            ),
            note = "Scope and deliverables to be finalised before publication."
        ),
        PricingPlanSpec(
            name = "Grow",
            audience = "Startups and growing businesses",
            priceLabel = "Price to be confirmed",
            isHighlighted = true,
            items = listOf(
                "Multi-page custom business website",
                "Business-focused structure and content layout",
                "Performance and SEO foundations",
                "Review rounds with your feedback"
            ),
            note = "Proposed structure — to be reviewed."
        ),
        PricingPlanSpec(
            name = "Scale",
            audience = "Established companies with bigger needs",
            priceLabel = "Custom quote",
            isHighlighted = false,
            items = listOf(
                "Custom website experience",
                "Advanced features scoped to your needs",
                "E-commerce where scope allows",
                "Priority maintenance option"
            ),
            note = "Final inclusions agreed per project."
        )
    )

    val comparisonRows = listOf(
        PlanComparisonRow("Responsive design", true, true, true),
        PlanComparisonRow("Contact form and basic SEO", true, true, true),
        PlanComparisonRow("Multi-page structure", false, true, true),
        PlanComparisonRow("Custom features", false, false, true),
        PlanComparisonRow("E-commerce (where scope allows)", false, false, true),
        PlanComparisonRow("Priority maintenance option", false, false, true)
    )

    val phases = listOf(
        ProcessPhaseSpec(
            phaseLabel = "Phase 1",
            title = "Discover",
            steps = listOf(
                ProcessStepSpec(1, "Discover TRUE RYZE", "Learn what we offer and whether it fits."),
                ProcessStepSpec(2, "Share your requirements", "Send an enquiry describing your business and needs.")
            )
        ),
        ProcessPhaseSpec(
            phaseLabel = "Phase 2",
            title = "Understand & agree",
            steps = listOf(
                ProcessStepSpec(3, "We understand your business", "Audience, goals and vision come first."),
                ProcessStepSpec(4, "Discuss the solution", "We propose an approach in plain language."),
                ProcessStepSpec(5, "Confirm scope and pricing", "Deliverables and cost agreed before work begins.")
            )
        ),
        ProcessPhaseSpec(
            phaseLabel = "Phase 3",
            title = "Build & review",
            steps = listOf(
                ProcessStepSpec(6, "Development begins", "Design and build get underway."),
                ProcessStepSpec(7, "Review and feedback", "You review progress and tell us what to adjust."),
                ProcessStepSpec(8, "Testing and quality checks", "We check everything before launch.")
            )
        ),
        ProcessPhaseSpec(
            phaseLabel = "Phase 4",
            title = "Launch & support",
            steps = listOf(
                ProcessStepSpec(9, "Launch and handover", "Your site goes live and is handed over to you."),
                ProcessStepSpec(10, "Maintenance and support", "Ongoing care after launch, where you choose it.", isOptional = true)
            )
        )
    )

    val lifecycleStages = listOf(
        LifecycleStageSpec(
            index = 0,
            numberLabel = "01",
            title = "Enquiry received",
            phase = "Phase 1 · Discover",
            description = "We log your business goals, target audience, and initial enquiry details."
        ),
        LifecycleStageSpec(
            index = 1,
            numberLabel = "02",
            title = "Requirements gathering",
            phase = "Phase 1 · Discover",
            description = "We clarify pages, features, content ownership, and any existing domain or hosting details."
        ),
        LifecycleStageSpec(
            index = 2,
            numberLabel = "03",
            title = "Project confirmed",
            phase = "Phase 2 · Understand & agree",
            description = "Scope, deliverables, review rounds, and pricing are agreed in writing before work starts."
        ),
        LifecycleStageSpec(
            index = 3,
            numberLabel = "04",
            title = "Design in progress",
            phase = "Phase 3 · Build & review",
            description = "Responsive layout architecture, visual hierarchy, and page structure take shape."
        ),
        LifecycleStageSpec(
            index = 4,
            numberLabel = "05",
            title = "Development in progress",
            phase = "Phase 3 · Build & review",
            description = "Frontend and functional build progresses across phone, tablet, and desktop breakpoints."
        ),
        LifecycleStageSpec(
            index = 5,
            numberLabel = "06",
            title = "Client review",
            phase = "Phase 3 · Build & review",
            description = "You review the working build and share requested adjustments in agreed review rounds."
        ),
        LifecycleStageSpec(
            index = 6,
            numberLabel = "07",
            title = "Testing and QA",
            phase = "Phase 3 · Build & review",
            description = "Cross-device checks, accessibility, performance, and form verification before release."
        ),
        LifecycleStageSpec(
            index = 7,
            numberLabel = "08",
            title = "Deployment",
            phase = "Phase 4 · Launch & support",
            description = "Production domain, SSL, and final launch configuration are prepared and verified."
        ),
        LifecycleStageSpec(
            index = 8,
            numberLabel = "09",
            title = "Completed",
            phase = "Phase 4 · Launch & support",
            description = "Your website goes live and is cleanly handed over to your business."
        ),
        LifecycleStageSpec(
            index = 9,
            numberLabel = "10",
            title = "Maintenance",
            phase = "Phase 4 · Launch & support",
            description = "Optional ongoing content updates, fixes, security upkeep, and technical support.",
            isOptional = true
        )
    )

    val portalStages = lifecycleStages.map { it.title }

    fun deriveClientLifecycleStage(
        clientStatus: String?,
        hasEnquiry: Boolean,
        hasAccessRequest: Boolean
    ): Int = when {
        clientStatus.equals("active", ignoreCase = true) -> 2
        hasAccessRequest -> 1
        hasEnquiry -> 0
        else -> -1
    }

    val faqs = listOf(
        FaqItem(
            "What can TRUE RYZE do for me today?",
            "Build your website and maintain it afterwards. Other services are coming soon and aren't yet offered."
        ),
        FaqItem(
            "How much does it cost?",
            "Pricing is being finalised. Send an enquiry and we'll discuss your needs and confirm scope and cost before any work begins."
        ),
        FaqItem(
            "Can you build an online shop?",
            "Where it fits your requirements, yes. Tell us what you need and we'll confirm what's realistic."
        ),
        FaqItem(
            "Do you work with international clients?",
            "Yes. Our team works remotely with clients worldwide."
        )
    )

    val serviceOptions = listOf(
        "Website development",
        "Website maintenance",
        "Both",
        "Not sure yet"
    )

    val planOptions = listOf(
        "Not sure yet",
        "Launch",
        "Grow",
        "Scale"
    )

    val budgetOptions = listOf(
        "Prefer to discuss",
        "Under 500 USD",
        "500–1,500 USD",
        "1,500–5,000 USD",
        "5,000+ USD"
    )

    val supportCategories = listOf(
        "Website issue",
        "Content change",
        "Billing",
        "Other"
    )

    val supportPriorities = listOf("Normal", "Urgent")

    // Clearly labelled sample preview datasets matching PORTAL_ARCHITECTURE.md & app.js
    val sampleClientPointsSeries = listOf(0, 600, 1500, 1800, 2100, 2400)
    val sampleClientHistory = listOf(
        "+600" to "Project completed · Aug",
        "+300" to "Referral · Jul",
        "+1,500" to "Welcome bond · Jun"
    )
    val sampleClientFeed = listOf(
        "Design approved" to "2 days ago",
        "Homepage build started" to "5 days ago",
        "Payment received (sample)" to "2 weeks ago"
    )

    data class SampleTeamWork(
        val workId: String,
        val clientCode: String,
        val task: String,
        val deadline: String,
        val status: String,
        val pay: String
    )

    val sampleTeamWorks = listOf(
        SampleTeamWork("W-2041", "TR-C-000123", "Build homepage sections", "24 Nov", "In progress", "USD 140 (sample)"),
        SampleTeamWork("W-2044", "TR-C-000187", "Fix mobile menu bug", "21 Nov", "In review", "USD 35 (sample)"),
        SampleTeamWork("W-2049", "TR-C-000201", "Content update", "30 Nov", "Not started", "USD 25 (sample)")
    )

    data class SampleDealerOrder(
        val clientCode: String,
        val service: String,
        val teamCode: String,
        val stage: String,
        val deadline: String,
        val isDueSoon: Boolean
    )

    val sampleDealerOrders = listOf(
        SampleDealerOrder("TR-C-000123", "Website development", "TR-T-0457", "Development", "28 Nov", false),
        SampleDealerOrder("TR-C-000187", "Website maintenance", "TR-T-0457", "Review", "21 Nov", true),
        SampleDealerOrder("TR-C-000201", "Content update", "TR-T-0460", "Not started", "30 Nov", false),
        SampleDealerOrder("TR-C-000144", "Landing page", "TR-T-0460", "Testing", "19 Nov", true),
        SampleDealerOrder("TR-C-000099", "Website development", "TR-T-0471", "Client review", "05 Dec", false)
    )

    val sampleDealerRequests = listOf(
        "Sample applicant A" to "Retail shop · website + maintenance",
        "Sample applicant B" to "Startup · landing page"
    )

    val sampleDealerTeams = listOf(
        "TR-T-0457" to 3,
        "TR-T-0460" to 2,
        "TR-T-0471" to 1
    )
}
