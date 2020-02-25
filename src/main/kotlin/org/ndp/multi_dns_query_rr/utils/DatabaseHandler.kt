package org.ndp.multi_dns_query_rr.utils

import me.liuwj.ktorm.database.Database
import me.liuwj.ktorm.dsl.*
import org.ndp.multi_dns_query_rr.bean.BatchInsertDNSDomain
import org.ndp.multi_dns_query_rr.bean.BatchInsertDNSIP
import org.ndp.multi_dns_query_rr.bean.Task
import org.ndp.multi_dns_query_rr.table.DNSDomain
import org.ndp.multi_dns_query_rr.table.DNSIP
import org.ndp.multi_dns_query_rr.utils.Logger.logger
import org.ndp.multi_dns_query_rr.table.Task as TableTask

object DatabaseHandler {
    private val dbUrl = Settings.setting["dbUrl"] as String
    private val dbDriver = Settings.setting["dbDriver"] as String
    private val dbUser = Settings.setting["dbUser"] as String
    private val dbPassword = Settings.setting["dbPassword"] as String
    private val database: Database


    init {
        database = Database.Companion.connect(
            dbUrl,
            dbDriver,
            dbUser,
            dbPassword
        )
    }

    fun batchUpdateTaskStatus(updateTasks: List<Task>) {
        logger.debug("task size: ${updateTasks.size}")
        TableTask.batchUpdate {
            for (task in updateTasks) {
                item {
                    it.taskStatus to task.status
                    it.desc to task.desc
                    where {
                        TableTask.id eq task.id
                    }
                }
            }
        }
    }

    fun batchInsertDNSIP(updateDNSIP: List<BatchInsertDNSIP>) {
        logger.debug("dns_ip size: ${updateDNSIP.size}")
        DNSIP.batchInsert {
            for (u in updateDNSIP) {
                item {
                    DNSIP.dns to u.dns
                    DNSIP.aRecord to u.aRecord
                }
            }
        }
    }

    fun batchInsertDNSDomain(updateDNSDomain: List<BatchInsertDNSDomain>) {
        logger.debug("dns_domain: ${updateDNSDomain.size}")
        DNSDomain.batchInsert {
            for (u in updateDNSDomain) {
                item {
                    DNSDomain.dns to u.dns
                    DNSDomain.domainID to u.domainID
                }
            }
        }
    }

    fun selectDomainID(domain: String): Int {
        val hashKey = OtherTools.digestMD5(domain)

        val check = database.useConnection { conn ->
            val sql = """
                select id from page use index(page_domain_hash_index) 
                where domain_hash = ? and domain = ?
            """
            conn.prepareStatement(sql).use { stmt ->
                stmt.setLong(1, hashKey)
                stmt.setString(2, domain)

                stmt.executeQuery().iterable().map { it.getInt(1) }
            }
        }

        return if (check.isEmpty()) {
            -1
        } else {
            check[0]
        }
    }
}