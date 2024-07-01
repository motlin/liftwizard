**Auditing** means tracking who performed each create and update operation.

The version object is a convenient place to store this information. Each version has a createdOn timestamp, and createdBy and lastUpdatedBy fields.

:include-json: non-destructive-updates2.json {
include: "$['absent', 'version']",
highlightValue: ["root.version.createdOn", "root.version.createdBy", "root.version.createdBy.userId", "root.version.lastUpdatedBy", "root.version.lastUpdatedBy.userId"]
}

The version table includes column createdById and lastUpdatedById that point to a user table.

## Deletes

