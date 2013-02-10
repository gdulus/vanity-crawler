modules = {
    application {
        resource url:'js/application.js'
    }

    bootstrap {
        resource url:'js/bootstrap.min.js'
        resource url:'css/bootstrap.min.css'
    }

    crawler {
        dependsOn 'bootstrap'
        resource url: 'css/crawler.css'
    }
}