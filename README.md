# Error handling in GraphQL API written in Scala

[![Build Status](https://travis-ci.org/kmusienko/graphql-error-handling.svg?branch=master)](https://travis-ci.org/kmusienko/graphql-error-handling) 

This is an example of a GraphQL server written with Play framework and Sangria that shows how to implement error handling in GraphQL API.

## How to start

Run the application:

`sbt run`

After launch, the application will be accessible on `localhost:9000`. The application uses in-memory H2 database that
is initialized by Play evolutions. When you start the application, you will see an error page with a suggestion to run 
the SQL script. Click `Apply this script now!`.

## Sources

https://graphql.org/

https://www.playframework.com/documentation/2.6.x/ScalaHome

https://sangria-graphql.org/learn/

https://facebook.github.io/graphql/#sec-Errors