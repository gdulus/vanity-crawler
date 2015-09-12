package vanity.crawler.stats

import groovy.sql.Sql
import vanity.article.ContentSource

import javax.sql.DataSource

class StatsService {

    private static final int DAYS_IN_THE_PAST = 30

    static transactional = false

    DataSource dataSource

    public def getCountPerDay() {
        def sources = ContentSource.list()
        def result = [days: [], values: [:]]
        (new Date() - DAYS_IN_THE_PAST..new Date()).collect { it.clearTime().toTimestamp() }.each { def date ->
            withSql { Sql sql ->
                def rows = sql.rows("""
                    select
                        source_id,
                        count(*) as count
                    from
                        article
                    where
                        date_created::date = ${date}
                    group by
                        date_created::date,
                        source_id
                """)

                result.days << date
                sources.each { ContentSource source ->
                    if (!result.values[source.target]) {
                        result.values[source.target] = []
                    }

                    def row = rows.find { it.source_id == source.id }
                    result.values[source.target] << (row ? row.count : 0)
                }
            }
        }

        return result
    }

    private def withSql(final Closure worker) {
        Sql sql = new Sql(dataSource)
        try {
            return worker.call(sql)
        } finally {
            sql.close()
        }
    }

}
