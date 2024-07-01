We've already seen version numbers in some examples. When we edited our Blueprint, the version number increased from 1 to 2.

When querying for previous data, version numbers can be more convenient than timestamps. We'll see this in the section on querying by version.

With versioning, we bump the version number when we edit any data within the composite. For Blueprints, this means that we bump the version number when we edit the Blueprint itself, when we replace the ImgurImage, when we replace the blueprint string, and when we add or remove tags. We'll take a closer look in the section on Composites.

# Query as-of version

As-of queries by version over rest are performed by adding a `version` query parameter to the URL.

Our template is `GET /api/blueprint/{blueprintKey}?version={version}`.

Plugging in the key from our running example, and the version number 1, we GET `/api/blueprint/6ed1f638-a63c-3a54-af67-ba494f27bff2?version=1`

This is similar to the as-of query by timestamp from the previous section, and the response is identical so we won't repeat it here.

# Version queries in SQL

At the SQL layer, queries by version number are implemented by starting with the version table.

```sql
select *
from BLUEPRINT_VERSION t0
where t0.key = '6ed1f638-a63c-3a54-af67-ba494f27bff2'
  and t0.number = 1
```

This query returns version 1, which existed for the duration `[2001-01-03, 2001-01-04)`.

:include-table: versioning-1-BlueprintVersion.csv {wide: true, title: "BlueprintVersion", systemFrom: {width: 215, align: "right"}, systemTo: {width: 215, align: "right"}}

At this point we take the system_from value of `2001-01-03 23:59:59.000` and use it in our subsequent queries. The queries on all other tables are identical to the queries in the previous section.

For example, to query the BLUEPRINT table:

```sql
select *
from BLUEPRINT t0
where t0.key = '6ed1f638-a63c-3a54-af67-ba494f27bff2'
  and t0.system_from <= '2001-01-03 23:59:59.000'
  and t0.system_to > '2001-01-03 23:59:59.000'
```

# Composites

In the previous example, we edited the Blueprint's title and markdown description, creating version 2.

Now we'll replace the Blueprint string, the ImgurImage, and add two more tags. We want to bump the version number just once more, to 3.

We update the blueprint by `PATCH`ing `/api/blueprint/{id}?version=2`.

:include-json: versioning-2.json {title: "PATCH /api/blueprint/{id}?version=2"}

# Response

As desired, we performed the several edits while bumping the version number by only one.

In addition, all of the new `systemFrom` times are identical: `2001-01-05T23:59:59Z`.

At the SQL level, all the edits were performed in a single transaction.

:include-file: versioning-3.diff {title: "PATCH /api/blueprint/{id}?version=2 response"}

# Ownership direction

`BlueprintTag` sits in the middle of a many-to-many relationship between `Blueprint` and `Tag`. In this example, we considered `BlueprintTag` to be part of the composite making up the `Blueprint`. Should we also consider it to be part of the `Tag` as well?

:include-image: factorio-nomnoml.png {fit: true}

This is our choice as application designers. In this case, it makes sense for `BlueprintTag` to be part of the `Blueprint`, but not part of `Tag`.

Stack Overflow makes a similar choice. `Questions` and `Tag`s are both versioned. Applying new tags to a question creates a new version of the `Question`, but not the `Tag`.

In the UML diagram above, composite relationships are denoted by black diamonds.

Composites are subtle, so let's walk through a few examples.

* Editing a Blueprint's title or description creates a new version. These are properties directly on the root type.
* Adding or removing BlueprintTag mappings creates a new version. These objects live within the composite.
* BlueprintTag mappings don't have any mutable properties. If they did, editing those properties would create a new version. For example, if we persisted their relative ordering with an ordinal property, then reordering the Blueprint's tags would create a new version.
* When the Blueprint author changes their display name, this does not create a new version. The User object is not part of the composite.
* We don't allow reassigning Blueprints to another author. If we did, repointing the author would create a new version. This works well with a temporal schema, because Blueprint.createdById would be swapped.

