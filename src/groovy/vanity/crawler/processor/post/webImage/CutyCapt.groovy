package vanity.crawler.processor.post.webImage

import groovy.transform.PackageScope

/**
 * sudo apt-get install cutycapt
 * http://cutycapt.sourceforge.net/
 */
@PackageScope
class CutyCapt {

    private static final String EXECUTABLE = 'cutycapt'

    private Params params = new Params()

    private String target

    CutyCapt(final String source, final String target) {
        this.target = target
        this.params.set('url', source)
        this.params.set('out', target)
    }

    public void setNoJavaScript(){
        params.set('javascript', 'off')
    }

    public File execute(){
        // execute process
        String command = "${EXECUTABLE} ${params.serialize()}"
        Process process = command.execute()
        // evaluate if process returned valid status
        if(process.waitFor() != 0){
            throw new Exception("Can't execute ${command} - ${process.err.text}")
        }

        File file = new File(target)

        if (!file.exists()){
            throw new Exception("There is no file stored as a result of ${command}")
        }

        return file
    }

    private static class Params {

        private Map<String, String> params = [:]

        public void set(final String param, final String value){
            params[param] = value
        }

        public String serialize(){
            return params.inject(''){result, it -> "${result} --${it.key}=${it.value}"}
        }

        @Override
        public String toString() {
            return serialize()
        }
    }
}
