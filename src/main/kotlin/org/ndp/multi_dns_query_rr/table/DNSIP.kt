package org.ndp.multi_dns_query_rr.table

import me.liuwj.ktorm.schema.Table
import me.liuwj.ktorm.schema.int
import me.liuwj.ktorm.schema.long

object DNSIP : Table<Nothing>("dns_ip")  {
    val id by int("id").primaryKey()
    val dns by long("dns")
    val aRecord by long("a_record")
}