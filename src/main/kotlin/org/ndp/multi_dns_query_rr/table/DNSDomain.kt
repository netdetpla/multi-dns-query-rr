package org.ndp.multi_dns_query_rr.table

import me.liuwj.ktorm.schema.Table
import me.liuwj.ktorm.schema.int
import me.liuwj.ktorm.schema.long
import org.ndp.multi_dns_query_rr.table.DNSIP.primaryKey

object DNSDomain : Table<Nothing>("dns_domain") {
    val id by int("id").primaryKey()
    val dns by long("dns")
    val domainID by int("domain_id")
}