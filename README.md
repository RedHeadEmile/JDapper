[![](https://jitpack.io/v/RedHeadEmile/JDapper.svg)](https://jitpack.io/#RedHeadEmile/JDapper)

# JDapperAPI

This project is a Dapper-like library in Java to map your query result with objects just like Dapper in C#.

All the functions you need are in the `JDapper` class. You can join up to 7 tables. The JDapper API will return a `RowMapper` which you can pass when calling the `JdbcTemplate.query`.