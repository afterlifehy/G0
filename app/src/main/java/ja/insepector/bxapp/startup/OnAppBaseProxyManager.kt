package ja.insepector.bxapp.startup

import ja.insepector.base.proxy.OnAppBaseProxyLinsener
import ja.insepector.bxapp.BuildConfig

class OnAppBaseProxyManager : OnAppBaseProxyLinsener {
    override fun onIsProxy(): Boolean {
        return BuildConfig.is_proxy
    }

    override fun onIsDebug(): Boolean {
        return BuildConfig.is_debug
    }

}