grails {
    development {
        mongo {
            host = db.development.mongodb.host
            port = db.development.mongodb.port
            databaseName = db.development.mongodb.databaseName
            username = db.development.mongodb.login
            password = db.development.mongodb.password
        }
    }

    test {
        mongo {
            host = db.test.mongodb.host
            port = db.test.mongodb.port
            databaseName = db.test.mongodb.databaseName
            username = db.test.mongodb.login
            password = db.test.mongodb.password
        }
    }

    production {
        mongo {
            host = db.production.mongodb.host
            port = db.production.mongodb.port
            databaseName = db.production.mongodb.databaseName
            username = db.production.mongodb.login
            password = db.production.mongodb.password
        }
    }
}
