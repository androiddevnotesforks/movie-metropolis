package movie.metropolis.app.model.adapter

/*
data class MembershipViewFeature(
    private val user: Customer
) : MembershipView {

    private val date = DateFormat.getDateInstance(DateFormat.MEDIUM)
    private val number = NumberFormat.getNumberInstance()

    private val membership get() = checkNotNull(user.membership)

    override val isExpired: Boolean
        get() = membership.expiration.before(Date())
    override val cardNumber: String
        get() = membership.number
    override val memberFrom: String
        get() = date.format(membership.inception)
    override val memberUntil: String
        get() = date.format(membership.expiration)
    override val daysRemaining: String
        get() = (membership.expiration.time - Date().time).milliseconds.inWholeDays.toString() + "d"
    override val points: String
        get() = number.format(user.membership?.points ?: 0)

}*/
