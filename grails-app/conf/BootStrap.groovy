import vanity.utils.BootstrapUtils

class BootStrap {

    def init = { servletContext ->
        BootstrapUtils.init()
    }
    def destroy = {
    }
}
