# Reladomo overview

[Reladomo](https://github.com/goldmansachs/reladomo) is an object-relational mapping (ORM) framework for Java with strong support for temporal and bitemporal data.

A comparison between Reladomo and Hibernate is out of scope for this guide. However, we'll look at how to use Reladomo in a Dropwizard application by replacing Hibernate with Reladomo in [`dropwizard-example`](https://github.com/dropwizard/dropwizard/tree/master/dropwizard-example).

After achieving feature parity, we'll see some of the power of Reladomo by adding support for as-of queries over REST.

To replace Hibernate with Reladomo in `liftwizard-example`, we'll need to:

* Create separate DTOs and POJOs
* Change the rest resources to return DTOs
* Create a Reladomo definition of the Person class
* Create a Reladomo class list and runtime configuration
* Replace `HibernateBundle` with `H2Bundle`, `NamedDataSourceBundle`, `ConnectionManagerBundle`, `ConnectionManagerHolderBundle`, and `ReladomoBundle`
* Replace the queries inside PersonDAO
* Add temporal columns and a sequence table to the database migrations

