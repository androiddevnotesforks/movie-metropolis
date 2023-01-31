package movie.metropolis.app.presentation.settings

class SettingsFacadeReactive(
    private val origin: SettingsFacadeFromFeature
) : SettingsFacade by origin {

    override var filterSeen: Boolean
        get() = origin.filterSeen
        set(value) {
            origin.filterSeen = value
            notifyListeners()
        }

    override var onlyMovies: Boolean
        get() = origin.onlyMovies
        set(value) {
            origin.onlyMovies = value
            notifyListeners()
        }

    override var clipRadius: Int
        get() = origin.clipRadius
        set(value) {
            origin.clipRadius = value
            notifyListeners()
        }

    override fun selectCalendar(id: String?) {
        origin.selectCalendar(id)
        notifyListeners()
    }

    private fun notifyListeners() = synchronized(origin.listeners) {
        for (listener in origin.listeners)
            listener.onChanged()
    }

}