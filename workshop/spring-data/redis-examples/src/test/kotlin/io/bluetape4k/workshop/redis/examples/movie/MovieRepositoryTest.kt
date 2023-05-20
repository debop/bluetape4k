package io.bluetape4k.workshop.redis.examples.movie

import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import io.bluetape4k.workshop.redis.examples.AbstractRedisTest
import io.bluetape4k.workshop.redis.examples.movie.model.Actor
import io.bluetape4k.workshop.redis.examples.movie.model.Movie
import io.bluetape4k.workshop.redis.examples.movie.model.MovieActor
import io.bluetape4k.workshop.redis.examples.movie.model.MovieActorReference
import io.bluetape4k.workshop.redis.examples.movie.repository.ActorRepository
import io.bluetape4k.workshop.redis.examples.movie.repository.MovieActorReferenceRepository
import io.bluetape4k.workshop.redis.examples.movie.repository.MovieActorRepository
import io.bluetape4k.workshop.redis.examples.movie.repository.MovieRepository
import io.bluetape4k.workshop.redis.examples.utils.buildExampleMatcher
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldContainSame
import org.amshove.kluent.shouldHaveSize
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Example
import org.springframework.data.domain.ExampleMatcher
import kotlin.test.assertFailsWith

class MovieRepositoryTest @Autowired constructor(
    private val actorRepo: ActorRepository,
    private val movieRepo: MovieRepository,
    private val movieActorRepo: MovieActorRepository,
    private val movieActorRefRepo: MovieActorReferenceRepository,
): AbstractRedisTest() {

    companion object: KLogging() {
        private val starwars = Movie("Star Wars", "SF", 1977)
        private val terminator = Movie("Terminator", "SF", 1984)
        private val avengers = Movie("Avengers", "Comics", 2018)

        val sampleMovies = listOf(starwars, terminator, avengers)

        private val harrisonFord = Actor("Harrison", "Ford")
        private val carrieFisher = Actor("Carrie", "Fisher")

        private val markHamill = Actor("Mark", "Hamill")
        private val arnoldSchwarzenegger = Actor("Arnold", "Schwarzenegger")

        private val lindaHamilton = Actor("Linda", "Hamilton")
        private val robertDowneyJr = Actor("Robert", "Downey Jr.")
        private val chrisEvans = Actor("Chris", "Evans")
    }

    @BeforeEach
    fun beforeEach() {
        actorRepo.deleteAll()
        movieRepo.deleteAll()
        movieActorRepo.deleteAll()
        movieActorRefRepo.deleteAll()
    }

    @Test
    fun `find all movies`() {
        movieRepo.saveAll(sampleMovies)
        actorRepo.saveAll(listOf(harrisonFord, carrieFisher, arnoldSchwarzenegger))

        val movies = movieRepo.findAll().toList()

        movies shouldHaveSize sampleMovies.size
        movies shouldContainSame sampleMovies
    }

    @Test
    fun `find movies by year`() {
        movieRepo.saveAll(sampleMovies)

        val movies = movieRepo.findByYear(1984)

        movies shouldHaveSize 1
        movies shouldBeEqualTo listOf(terminator)
    }

    @Test
    fun `find movies by example`() {
        movieRepo.saveAll(sampleMovies)

        // Movie::name 과 같은 정보를 가진 ExampleMatcher를 생성한다.
        val matcher = Movie::class.buildExampleMatcher(Movie::name.name)
            .withMatcher(Movie::name.name, ExampleMatcher.GenericPropertyMatchers.exact())
            .withIgnoreNullValues()

        val movieExample = Movie("Star Wars", "", 0)
        val example = Example.of<Movie>(movieExample, matcher)

        val found = movieRepo.findAll(example).toList()
        found shouldBeEqualTo listOf(starwars)
    }

    @Test
    fun `지정한 배우가 출연한 영화를 hashId로 검색한다`() {
        movieRepo.saveAll(sampleMovies)
        actorRepo.saveAll(listOf(harrisonFord, carrieFisher, arnoldSchwarzenegger))

        // Movie - Actor 연관 관계를 저장한다
        val starWarsHarrisonFord = MovieActor(starwars.hashId!!, harrisonFord.hashId!!)
        val terminatorArnold = MovieActor(terminator.hashId!!, arnoldSchwarzenegger.hashId!!)

        val movieActors = movieActorRepo.saveAll(listOf(starWarsHarrisonFord, terminatorArnold))
        movieActors.forEach {
            log.debug { "MovieActor: $it" }
        }

        // NOTE: 검색조건에 Reference를 넣지 못한다. Redis HashId 로 찾아야 한다
        // 해리슨 포드가 출연한 영화의 hashId 를 검색한다
        log.debug { "harrisonFord.hashId=${harrisonFord.hashId}" }
        val movieHashIds = movieActorRepo.findByActorHashId(harrisonFord.hashId!!).map { it.movieHashId }
        log.debug { "movieHashIds=$movieHashIds" }
        movieHashIds shouldHaveSize 1

        val movies = movieRepo.findAllById(movieHashIds).toList()
        movies shouldBeEqualTo listOf(starwars)
    }

    @Test
    fun `지정한 배우가 출연한 영화를 Actor Reference를 이용하여 검색은 지원하지 않는다`() {
        movieRepo.saveAll(sampleMovies)
        actorRepo.saveAll(listOf(harrisonFord, carrieFisher, arnoldSchwarzenegger))

        // Movie - Actor 연관 관계를 저장한다
        val starWarsHarrisonFord = MovieActorReference(starwars, harrisonFord)
        val terminatorArnold = MovieActorReference(terminator, arnoldSchwarzenegger)

        val refs = movieActorRefRepo.saveAll(listOf(starWarsHarrisonFord, terminatorArnold))
        log.debug { "movie-actor references=$refs" }

        // NOTE: 검색조건에 Reference를 넣지 못한다. Redis HashId 로 찾아야 한다
        //
        assertFailsWith<RuntimeException> {
            // 해리슨 포드가 출연한 영화 찾기 (Reference 로 검색하는 기능은 지원하지 않습니다)
            val movies = movieActorRefRepo.findByActor(harrisonFord).map { it.movie }

            movies.forEach {
                log.debug { "movie=$it" }
            }

            movies shouldBeEqualTo listOf(starwars)
        }
    }

    @Test
    fun `배우가 출연한 영화를 Example 을 이용하여 Reference 검색은 지원하지 않는다`() {
        movieRepo.saveAll(sampleMovies)
        actorRepo.saveAll(listOf(harrisonFord, carrieFisher, arnoldSchwarzenegger))

        // Movie - Actor 연관 관계를 저장한다
        val starWarsHarrisonFord = MovieActorReference(starwars, harrisonFord)
        val terminatorArnold = MovieActorReference(terminator, arnoldSchwarzenegger)

        val refs = movieActorRefRepo.saveAll(listOf(starWarsHarrisonFord, terminatorArnold))
        log.debug { "movie-actor references=$refs" }

        // Movie::name 과 같은 정보를 가진 ExampleMatcher를 생성한다.
        val matcher = MovieActorReference::class.buildExampleMatcher(MovieActorReference::actor.name)
            .withMatcher(MovieActorReference::actor.name, ExampleMatcher.GenericPropertyMatchers.exact())
            .withIgnoreNullValues()

        val movieExample = MovieActorReference(null, harrisonFord)
        val example = Example.of<MovieActorReference>(movieExample, matcher)

        // NOTE: 검색 시 Reference 속성은 Example로 검색은 안된다
        assertFailsWith<AssertionError> {
            val movies = movieActorRefRepo.findAll(example).map { it.movie!! }
            log.debug { "movies=$movies" }
            movies shouldHaveSize 1
            movies shouldContainSame listOf(starwars)
        }
    }
}
