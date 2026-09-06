# Questions

Here we have 3 questions related to the code base for you to answer. It is not about right or wrong, but more about what's the reasoning behind your decisions.

1. In this code base, we have some different implementation strategies when it comes to database access layer and manipulation. If you would maintain this code base, would you refactor any of those? Why?

**Answer:**
```txt
Store uses the Active Record pattern - the Store object itself knows how to save and find itself,
without needing a separate helper class. This fits because Store has no real business rules, just
basic create/read/update/delete, so no extra class is needed. Product uses the Repository pattern -
here, a separate helper class handles saving and finding Product records, instead of Product doing
it itself. This fits for the same reason as Store (simple, basic operations), just organized
slightly differently. In both Store and Product, the business rules (small checks like "this field
must be filled in") sit directly inside the same class that handles the web request and talks to
the database. Everything is mixed together in one place.

Warehouse uses the Ports and Adapters pattern instead. Here, the business rules are kept in their
own separate classes, and those classes never talk to the database or the web layer directly. They
only talk to a simple interface (Ports) that describes what they need. A separate class (adapter)
then handles the actual connection to the real database.

I would not force one single style everywhere. I would keep the simple style for simple features,
and only move a feature to the Ports and Adapters style once its rules actually become complex
enough to need that separation. Warehouse uses this pattern because its business rules are complex
enough to need testing and protection on their own, separate from the database and web layer. Store
and Product only have small, basic checks right now, so I would leave them exactly as they are
rather than rewriting them just for consistency. I did already do a small refactor of this kind
during this actual work - the Create and Replace warehouse classes had the same location and
capacity checks written out twice, so I pulled that shared logic into one common class instead.
That is the kind of small, focused refactor I would keep doing as real duplication shows up, rather
than redesigning the whole codebase upfront.
```
----
2. When it comes to API spec and endpoints handlers, we have an Open API yaml file for the `Warehouse` API from which we generate code, but for the other endpoints - `Product` and `Store` - we just coded directly everything. What would be your thoughts about what are the pros and cons of each approach and what would be your choice?

**Answer:**
```txt
Warehouse uses an API-first approach - the endpoints and their request/response shapes are
described upfront in an OpenAPI YAML file, and the Java interface plus the DTOs are generated
from that file. Store and Product instead use a code-first approach - the endpoints and their
JSON shapes are just written directly in Java, with no separate spec file at all.

There are some  benefits to API-first. The API can be agreed on and reviewed before any code is
written. This matters when several teams or outside users need to work with the same API. The
documentation also stays correct over time, since it comes from the same file as the code, so
there is no separate step to update it by hand. The same file can also be used later to generate
client code in other languages, if that is ever needed.
But it adds real complexity too. There is a generator tool, an extra build step, and generated
code that cannot be edited by hand - this is one more thing a new person has to learn. It also
splits the generated DTO from the internal model. This is good practice, but it means extra code
just to convert between the two, which a simple endpoint does not really need.

Code-first, like Store and Product, is much simpler - everything for one endpoint is in one file,
readable top to bottom, with no generation step or build-time magic to explain. The downside is
that there's no separation between the database entity and what gets exposed over the API - the
same class serves as both, so changing the database model directly changes the public API too.
If documentation is wanted, it has to be generated afterwards from annotations (e.g.
smallrye-openapi scanning) or written and kept up to date by hand, with the usual risk of it
going stale.

My choice: I would pick one approach for the whole project rather than mixing both like this
codebase currently does, since switching mental models per endpoint adds real overhead for
anyone maintaining it. For a small, single-team, internal project like this one, I would lean
towards code-first with an auto-generated-from-code Swagger/OpenAPI page 
for documentation - this keeps things simple like Store and Product, while still giving a
browsable, always-current list of endpoints. I would reserve full API-first generation for an API
that genuinely needs a contract agreed and shared across teams or outside consumers before any
code is written, which isn't really the situation here.
```
----
3. Given the need to balance thorough testing with time and resource constraints, how would you prioritize and implement tests for this project? Which types of tests would you focus on, and how would you ensure test coverage remains effective over time?

**Answer:**
```txt
I would prioritize by risk and complexity, not by trying to cover everything equally.

The type of test I would focus on most is small, fast unit tests for the actual business rules -
things like the warehouse capacity and location limits, what happens when replacing a warehouse,
and the fulfillment limit checks (how many warehouses can serve one product, one store, and so
on). Real bugs are most likely to hide here, and these tests are also the cheapest to write, since
the rules can be tested alone with a small fake version of the database, no real database needed.
While doing this work, I actually found and fixed several real bugs in this exact logic, which is
proof this layer benefits the most from tests.

Next, I would add a smaller number of integration tests - tests that call the real endpoints
against a real database, covering the main success case and a few realistic failure cases per
feature. These catch problems the business-rule tests alone cannot, like wrong HTTP status codes or database-level problems.
This is not just theory either - this exact kind of test is how I found a real bug where a
successful warehouse replacement fails against a real database, something the fake-based tests
never would have caught.


To keep coverage useful over time, I actually added JaCoCo to this project to measure it - it
shows exactly which classes and lines are tested and which are not, so gaps are visible instead of
guessed at. On top of that, I would enforce a minimum of 80% coverage directly in the Maven build
using JaCoCo's check rule, and run that same build inside a GitHub Actions workflow on every pull
request. That way, any change that drops coverage below that mark fails the build automatically,
instead of relying on someone remembering to check it by hand.
```