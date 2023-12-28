package ja.insepector.bxapp.mvvm.repository

import ja.insepector.base.base.mvvm.BaseRepository
import ja.insepector.base.bean.FeeRateResultBean
import ja.insepector.base.bean.HttpWrapper
import ja.insepector.base.bean.IncomeCountingBean
import ja.insepector.base.bean.UpdateBean

class MineRepository : BaseRepository() {
    /**
     * 版本更新查询
     */
    suspend fun checkUpdate(param: @JvmSuppressWildcards Map<String, Any?>): HttpWrapper<UpdateBean> {
        return mServer.checkUpdate(param)
    }

    /**
     * 营收打印
     */
    suspend fun incomeCounting(param: @JvmSuppressWildcards Map<String, Any?>): HttpWrapper<IncomeCountingBean> {
        return mServer.incomeCounting(param)
    }

    /**
     * 费率
     */
    suspend fun feeRate(param: @JvmSuppressWildcards Map<String, Any?>): HttpWrapper<FeeRateResultBean> {
        return mServer.feeRate(param)
    }
}