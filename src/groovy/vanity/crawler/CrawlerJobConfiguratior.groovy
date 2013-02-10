package vanity.crawler

import org.quartz.JobExecutionContext
import vanity.ContentSource

final class CrawlerJobConfiguratior {

    private static final String TRIGGER_GROUP = 'VANITY_CRAWLER'

    private static final int DEFAULT_START_DELAY = 60000 * 10       // 10 minute

    private static final int DEFAULT_REPEAT_INTERVAL = 60000 * 60   // one hour

    private static final int DEFAULT_REPEAT_COUNT = -1

    private static final String MANUAL_TRIGGER_KEY = 'source'

    public static Map<String, ?> buildConfigFor(final ContentSource contentSource){
        return [
            name:contentSource.name(),
            group:TRIGGER_GROUP,
            startDelay:DEFAULT_START_DELAY,
            repeatInterval: DEFAULT_REPEAT_INTERVAL,
            repeatCount: DEFAULT_REPEAT_COUNT
        ]
    }

    public static Map<String, ContentSource> getTriggerNowParam(final ContentSource contentSource){
        return [(MANUAL_TRIGGER_KEY):contentSource]
    }

    public static ContentSource getContentSource(final JobExecutionContext context){
        if (context.trigger.jobDataMap && context.trigger.jobDataMap.containsKey(MANUAL_TRIGGER_KEY)){
            return context.trigger.jobDataMap[MANUAL_TRIGGER_KEY]
        }

        if (context.trigger.group == TRIGGER_GROUP){
            return ContentSource.valueOf(context.trigger.name)
        }

        throw new IllegalArgumentException()
    }

}