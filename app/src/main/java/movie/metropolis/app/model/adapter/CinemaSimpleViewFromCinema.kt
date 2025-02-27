package movie.metropolis.app.model.adapter

import movie.cinema.city.Cinema
import movie.metropolis.app.model.CinemaSimpleView

data class CinemaSimpleViewFromCinema(
    val cinema: Cinema
) : CinemaSimpleView {

    override val id: String
        get() = cinema.id
    override val name: String
        get() = cinema.name
    override val city: String
        get() = cinema.address.city
    override val distance: String
        get() = "0km"
}