# Case Study Scenarios to discuss

## Scenario 1: Cost Allocation and Tracking
**Situation**: The company needs to track and allocate costs accurately across different Warehouses and Stores. The costs include labor, inventory, transportation, and overhead expenses.

**Task**: Discuss the challenges in accurately tracking and allocating costs in a fulfillment environment. Think about what are important considerations for this, what are previous experiences that you have you could related to this problem and elaborate some questions and considerations

**Questions you may have and considerations:**
```txt
A fulfillment environment like this one has costs in a few main areas:
- Labor - receiving, picking and packing staff at the Warehouse, staff receiving deliveries at the
  Store, and the people who plan which Warehouse serves which Store.
- Inventory - the cost of capital tied up in stock sitting in a Warehouse, the storage space it
  uses, and any loss from damage or spoilage while it sits there.
- Transportation - moving stock into a Warehouse, between Warehouses, and out to Stores, plus the
  vehicles and fuel behind all of that.
- Overhead - rent, utilities, equipment, and the systems used to run a Warehouse or Store day to
  day.

Tracking and allocating these matters because without it, a Warehouse or Store can look
profitable on paper while actually being expensive to run once its real share of these costs is
counted in. It is also what makes real business decisions possible - comparing two Warehouses to
see which is cheaper to run, deciding whether a Store is worth serving the way it currently is, or
checking whether replacing a Warehouse actually saved money afterwards. Without tracking and
allocation, none of those comparisons have real numbers behind them.

Some of the challenges I can think of are:

- Most costs aren't owned by one thing - they're shared, and someone has to decide how to split
  them. A delivery truck might serve three stores in one trip - how do you split that
  transportation cost fairly? By distance, by weight, by number of stops?

- Some costs don't change even if you ship more or less. Rent and overhead stay the same each
  month, so dividing them by a changing shipment volume makes "cost per unit" swing around, which
  can make a warehouse look worse (or better) than it actually is.

- The cost shows up in one place, but is really caused by something else. A warehouse can look
  expensive because of picking labor, when the real cause is a store ordering in lots of tiny,
  scattered batches instead of consolidated ones.

- Cost can be spent even when nothing gets delivered. Products can be packed and labor spent
  preparing a shipment, and then a store cancels before it goes out - that cost is already spent
  with nothing to show for it, and it is unclear who should be charged for it.

- Costs get recorded at a much bigger level than what you actually need to know. A delivery might
  carry 50 different products to one Store in a single trip, billed as one total cost - but
  working out a single product's real profit needs to know just that one product's share of it.
  Since the trip was never billed per product, that share always has to be estimated afterward,
  not looked up as a real recorded number.

Questions I would ask before starting:
- Does this need to be real-time (cost posted the moment something happens), or is a monthly/
  period-end allocation enough?
- Is this for internal reporting only, or does it feed the company's actual financial statements
  (which would need audit-grade accuracy and traceability, not just a rough estimate)?
- When a Warehouse serves several Stores (colocation), who owns a shared cost - split evenly,
  split by volume shipped, or by a pre-agreed contract term?
- How should a cost that was spent but produced no result (like a cancelled shipment) be treated -
  charged to whoever cancelled it, absorbed as a general loss, or tracked separately so it does not
  quietly distort everyone else's numbers?
```

## Scenario 2: Cost Optimization Strategies
**Situation**: The company wants to identify and implement cost optimization strategies for its fulfillment operations. The goal is to reduce overall costs without compromising service quality.

**Task**: Discuss potential cost optimization strategies for fulfillment operations and expected outcomes from that. How would you identify, prioritize and implement these strategies?

**Questions you may have and considerations:**
```txt
Here are some strategies I would consider:

- Serve from the closest Warehouse with capacity, use route optimization, and combine nearby
  deliveries into one trip. Instead of routing a Store to a distant Warehouse when a closer one has
  room, plan the smartest/shortest path for the stops a truck actually needs to make, and combine
  deliveries to stores that are near each other into fewer, fuller trips instead of separate ones.
  All three together directly cut total distance driven and number of trips - which is where most
  transportation cost actually comes from.
- Stop overstocking "just in case," and forecast demand better. Holding more inventory than needed
  ties up money and space that isn't earning anything back, and stock that sits too long also
  risks damage or going unsellable. Better forecasting means restocking closer to when it is
  actually needed, so less capital and space is wasted sitting idle.
- Match staff scheduling to real demand, and reduce picking errors. Overstaffing a quiet shift
  wastes labor cost, understaffing a busy one causes delays and mistakes. Picking in batches
  (several orders in one walk through the warehouse) and reducing mis-picks (a wrong pick costs
  labor twice - once to do it wrong, once to fix it) both cut wasted labor hours directly.
- Consolidate underused Warehouses instead of paying overhead on several half-empty ones. If two
  nearby Warehouses are both running well under capacity, merging into one better-utilized site
  cuts rent, utilities, and staffing overhead that were being paid twice for capacity that wasn't
  being used anyway.
- Confirm an order before packing starts, to avoid wasted labor on cancellations. This directly
  targets the "cost spent but nothing delivered" problem from Scenario 1 - packing labor spent on
  an order that gets cancelled afterward is pure waste. Confirming intent before committing labor
  avoids paying for work that produces nothing.
- Use the Fulfillment data itself to catch inefficient pairings, and keep the existing count limits
  enforced. The system already records which Warehouse serves which Store/product - that data can
  directly reveal a Store being served inefficiently, without needing anything new to be built.
  Keeping the existing 2/3/5 limits in place also stops resources from spreading too thin across
  too many Warehouses or products, which would quietly raise the overhead cost per unit.

Knowing which of these actually matter most needs real cost data first (Scenario 1) - without it,
this list is just guesses, not a real, prioritized plan. This also is not an exhaustive list -
there will likely be many more specific strategies once real analysis is done on actual cost and
fulfillment data, and the real, complete list only becomes clear once that analysis happens.

To identify which of these actually matter, I would look at real Fulfillment and cost data for
real problem patterns, rather than guessing from the list. To prioritize, I would start with the
cheap, fast, low-risk ones  before committing to slow, expensive,structural changes (like replacing 
or consolidating a Warehouse). To implement, I would pilot a
change on one Warehouse or Store first, and measure both the real cost saved and service quality,
before rolling it out wider.
```

## Scenario 3: Integration with Financial Systems
**Situation**: The Cost Control Tool needs to integrate with existing financial systems to ensure accurate and timely cost data. The integration should support real-time data synchronization and reporting.

**Task**: Discuss the importance of integrating the Cost Control Tool with financial systems. What benefits the company would have from that and how would you ensure seamless integration and data synchronization?

**Questions you may have and considerations:**
```txt
Without this connection, cost information sits in two different places - our system, and the
finance team's records. Over time, these two stop matching each other.
Someone then has to manually check both and fix the differences by hand. This takes a long time,
and mistakes happen easily. Because of this, the finance team cannot fully trust the numbers
coming from our system when they close their books each month.

Benefits from doing this properly:
- Faster closing - numbers flow automatically instead of being manually re-entered, cutting both
  time and mistakes.
- Faster decisions - finance sees a cost spike the same day, not next month at close.
- One number everyone trusts, instead of operations and finance quietly working off two different
  versions of the same cost.
- Easier to satisfy an audit, since data flows through one traceable pipeline instead of a
  spreadsheet someone typed by hand.
- It is also what actually makes Scenario 2 (optimization) and Scenario 4 (forecasting)
  trustworthy, since both depend on this same cost data being reliable.

Real-time sync is the hard part. There are two basic shapes: push (we publish an event every time
something cost-relevant happens - a Warehouse is created, replaced, or archived, a Fulfillment
assignment is made) or pull (the financial system polls us on a schedule). Given this system
already has clear moments where something happens (create/replace/archive), a push/event-based
approach fits naturally on top of what already exists, rather than the financial system having to
guess when to ask.

To keep this sync actually reliable, a few things matter beyond just picking push or pull:
- Agree on a clear data contract first - exactly what fields get sent - before writing any code,
  since most integration pain comes from both sides quietly assuming a different shape of data.
- Make every sync safe to repeat - if the same event gets sent twice because of a network retry,
  it must not get counted twice, so each event needs its own unique ID that the receiving side can
  use to spot and ignore a duplicate.
- Alert immediately if a sync fails, rather than letting it fail silently - a failed message should
  land somewhere visible (a dead-letter queue, an alert to the team) so someone can fix it the same
  day, not discover it weeks later.
- Run a periodic reconciliation check anyway, even with real-time sync, as a safety net for the
  failures real-time sync will not catch on its own.
```

## Scenario 4: Budgeting and Forecasting
**Situation**: The company needs to develop budgeting and forecasting capabilities for its fulfillment operations. The goal is to predict future costs and allocate resources effectively.

**Task**: Discuss the importance of budgeting and forecasting in fulfillment operations and what would you take into account designing a system to support accurate budgeting and forecasting?

**Questions you may have and considerations:**
```txt
Importance of budgeting and forecasting:
- Planning ahead, instead of reacting late. Fulfillment has some predictable patterns - busier
  during holidays, quieter other times. A forecast lets the company add staff and prepare
  warehouse space before the busy period starts, instead of scrambling once orders are already
  piling up, when it is too late to hire or expand in time.
- Avoiding money surprises. Rent, labor, and transport are costs that keep happening every month,
  not a one-time payment. Without a budget, a company can end up spending more each month than the
  business can actually support, and only realize this after the money is already spent.
- Big decisions about capacity need this. This system's own rules show why. A location can only
  hold a limited number of Warehouses, and a Warehouse can only hold a limited number of products.
  So growth eventually forces a real choice: open a new Warehouse, or replace an old one. That is
  a big, real amount of money to spend. Forecasting is what tells you whether the extra business
  coming in is actually worth that spend, instead of just guessing.
- Giving performance a number to compare against. A budget sets a target. Without one, there is no
  way to say "this is going well" or "something is wrong" - it is just a number with nothing to
  measure it against. The budget is what turns a plain cost number into a real signal of whether
  operations are healthy or not.

To design a system for this, I would take into account:
- Store the cost data by week or month, not just as one big total, and keep it tagged by
  Warehouse or Store, across enough years. This is what lets us actually see a pattern, like
  holiday peaks - a single "total cost so far" number cannot show a pattern like that at all.
- Track things like how full a Warehouse is, or how many active Fulfillment assignments it has,
  as their own separate numbers. These can hint at rising future cost even before that cost
  actually happens, so they should not stay hidden inside the raw data.
- Mark a Warehouse replace or archive as its own special event, not just another data point in
  the trend. A forecast normally expects a smooth line, but a replacement causes a sudden jump,
  so the system needs a clear way to flag "something changed here."
- Keep every old forecast saved, and later compare it to what actually happened. Without this,
  there is no way to know if the forecasting approach was any good, or to improve it over time.
- Save what each forecast assumed, and when it was made, not just the number itself. If a
  Warehouse gets replaced right after a forecast was made, that forecast is now outdated - but we
  can only know that if we also saved when it was made and what it was based on.
- The actual forecasting should use a real time-series forecasting model trained on this history,
  rather than someone manually projecting a number forward - this is exactly the kind of problem
  those models are built for.
```

## Scenario 5: Cost Control in Warehouse Replacement
**Situation**: The company is planning to replace an existing Warehouse with a new one. The new Warehouse will reuse the Business Unit Code of the old Warehouse. The old Warehouse will be archived, but its cost history must be preserved.

**Task**: Discuss the cost control aspects of replacing a Warehouse. Why is it important to preserve cost history and how this relates to keeping the new Warehouse operation within budget?

**Questions you may have and considerations:**
```txt
Cost control aspects of replacing a Warehouse means all the different places where money is
involved in this action, and what needs to be watched or checked so spending does not quietly go
wrong. It is not just "what does the replace cost" - it covers both the one-time cost of doing the
replace itself, and the ongoing cost of running the new Warehouse compared to the old one.

Cost control aspects:

- The existing rules already protect money, even though they don't look like it. The system
  already checks that the new warehouse can hold all the stock from the old one, and that the
  stock numbers match exactly. This sounds like just a data check, but it's really protecting
  money: if the new warehouse is too small, you'd need extra emergency storage somewhere else
  (that costs money). If the stock numbers don't match, some inventory could quietly go missing
  (inventory is money too). So this rule is already doing cost control, just not labeled that way.

- Replacing a warehouse costs real money - new lease, moving costs, closing the old site. Right now, 
  anyone can trigger a replace and nothing checks "did finance actually approve spending this money 
  first?" Cost control often means getting a yes from whoever controls the budget before spending, 
  not just checking the numbers afterward.

- Old and new warehouse might both cost money at the same time for a while. When switching over,
  there could be a short period where the old warehouse is still finishing up (still costs money)
  while the new one has already started running (also costs money). If you only compare "before
  the switch" and "after the switch" as two separate periods, you might miss this middle period
  where you were basically paying for two warehouses at once.

- The cost of the move itself should be checked separately from the cost of running the new
  warehouse. Moving inventory and shutting down the old site is a one-time cost. Running the new
  warehouse day-to-day is an ongoing cost. These are two different things - you should check "did
  the move cost what we expected" on its own, separately from "is the new warehouse cheap or
  expensive to run long term."

- The ongoing cost, before versus after, needs to be compared. Once the new Warehouse is running,
  its monthly cost (rent, staff, utilities) should be compared against what the old Warehouse used
  to cost. This can only be done if the old Warehouse's cost history was kept and compared under
  the same Business Unit Code, since the two are treated as one continuous operational area.

- Whether the replace actually paid for itself. Putting the one-time move cost and the ongoing
  monthly saving together tells you how long it takes before the replace is actually worth it.
  Before that point is reached, the replace has technically cost more than it has saved so far,
  even if it turns out to be a good decision long term.

Why preserving cost history matters, and how it relates to budget:

The Business Unit Code represents one continuous "area" of the business, even though the physical
Warehouse behind it changes when it is replaced. If cost history gets lost or reset the moment a
replace happens, there is no way to answer the most basic question about the whole replacement -
did it actually help, or not? The entire point of doing a replace is usually to save money or
improve something, but "did it work" cannot be answered without a "before" number to compare
against. Losing the history does not just lose data, it loses the ability to judge the decision
at all.

"Within budget" only means something if there is a real target to measure against. For a brand
new Warehouse, the most natural, honest target is not a number pulled out of thin air - it is the
old Warehouse's actual cost, since that is the baseline the replace was measured against in the
first place. Without preserved history, there is no real number to set the new Warehouse's budget
target from, it becomes a guess instead of something grounded in the business's own past
performance. If the new Warehouse's costs quietly creep up over time, there is also nothing to
compare against to catch it. So preserving history is why the replace can be judged at all, and
staying within budget is that judgement, made concrete as an ongoing number to track.
```

## Instructions for Candidates
Before starting the case study, read the [BRIEFING.md](BRIEFING.md) to quickly understand the domain, entities, business rules, and other relevant details.

**Analyze the Scenarios**: Carefully analyze each scenario and consider the tasks provided. To make informed decisions about the project's scope and ensure valuable outcomes, what key information would you seek to gather before defining the boundaries of the work? Your goal is to bridge technical aspects with business value, bringing a high level discussion; no need to deep dive.
