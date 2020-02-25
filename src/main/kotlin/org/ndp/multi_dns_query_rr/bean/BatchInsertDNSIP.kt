package org.ndp.multi_dns_query_rr.bean

data class BatchInsertDNSIP(
    val dns: Long,
    val aRecord: Long
)