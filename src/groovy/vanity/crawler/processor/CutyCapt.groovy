package vanity.crawler.processor

import groovy.transform.PackageScope

@PackageScope
class CutyCapt {

    private static final String EXECUTABLE = 'cutycapt'

    private String params = ""

    private String target

    private String source

    CutyCapt(final String source, final String target) {
        this.source = source
        this.target = target
    }

    public void setNoJavaScript(){
        params = "--javascript=off"
    }

    public File execute(){
        // execute process
        String command = "${EXECUTABLE} ${params} --url=${source} --out=${target}"
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
}
