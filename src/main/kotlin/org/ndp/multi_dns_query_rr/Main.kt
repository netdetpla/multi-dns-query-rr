package org.ndp.multi_dns_query_rr

import org.ndp.multi_dns_query_rr.bean.BatchInsertDNSDomain
import org.ndp.multi_dns_query_rr.bean.BatchInsertDNSIP
import org.ndp.multi_dns_query_rr.bean.Task
import org.ndp.multi_dns_query_rr.utils.DatabaseHandler
import org.ndp.multi_dns_query_rr.utils.Logger.logger
import org.ndp.multi_dns_query_rr.utils.OtherTools
import org.ndp.multi_dns_query_rr.utils.RedisHandler

object Main {
    @JvmStatic
    fun main(args: Array<String>) {
        logger.info("start result recycling...")
        val results = RedisHandler.consumeResult(RedisHandler.generateNonce(5))
        RedisHandler.returnACK()
        val updateTasks = ArrayList<Task>()
        val updateDNSIP = ArrayList<BatchInsertDNSIP>()
        val updateDNSDomain = ArrayList<BatchInsertDNSDomain>()
        for (r in results) {
            // task status update
            if (r.status == 1) {
                updateTasks.add(Task(r.taskID, 21000, r.desc))
                continue
            }
            updateTasks.add(Task(r.taskID, 20030, ""))

            for (rr in r.result) {
                for (a in rr.aRecord) {
                    updateDNSIP.add(
                        BatchInsertDNSIP(
                            OtherTools.iNetString2Number(rr.dnsServer),
                            OtherTools.iNetString2Number(a)
                        )
                    )
                }
                updateDNSDomain.add(
                    BatchInsertDNSDomain(
                        OtherTools.iNetString2Number(rr.dnsServer),
                        DatabaseHandler.selectDomainID(rr.domain)
                    )
                )
            }
        }
        DatabaseHandler.batchInsertDNSDomain(updateDNSDomain)
        DatabaseHandler.batchInsertDNSIP(updateDNSIP)
        DatabaseHandler.batchUpdateTaskStatus(updateTasks)
    }
}