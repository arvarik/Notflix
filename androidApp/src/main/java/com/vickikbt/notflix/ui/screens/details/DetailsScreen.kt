package com.vickikbt.notflix.ui.screens.details

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.navigation.NavController
import coil.annotation.ExperimentalCoilApi
import coil.compose.ImagePainter
import coil.compose.rememberImagePainter
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.fade
import com.google.accompanist.placeholder.placeholder
import com.google.accompanist.placeholder.shimmer
import com.vickikbt.notflix.R
import com.vickikbt.notflix.ui.components.ItemMovieCast
import com.vickikbt.notflix.ui.components.ItemSimilarMovies
import com.vickikbt.notflix.ui.components.MovieRatingSection
import com.vickikbt.notflix.ui.components.app_bars.DetailsAppBar
import com.vickikbt.notflix.ui.theme.Gray
import com.vickikbt.notflix.ui.theme.TextSecondary
import com.vickikbt.notflix.util.*
import com.vickikbt.shared.domain.models.MovieDetails
import com.vickikbt.shared.presentation.presenters.SharedDetailsPresenter
import me.onebone.toolbar.CollapsingToolbarScaffold
import me.onebone.toolbar.ScrollStrategy
import me.onebone.toolbar.rememberCollapsingToolbarScaffoldState
import org.koin.androidx.compose.get

@Composable
fun DetailsScreen(
    navController: NavController,
    detailsViewModel: SharedDetailsPresenter = get(),
    movieId: Int,
    cacheId: Int,
) {

    LaunchedEffect(key1 = detailsViewModel) {
        detailsViewModel.getIsMovieFavorite(movieId)
        detailsViewModel.getMovieDetails(movieId)
        detailsViewModel.fetchSimilarMovies(movieId)
        detailsViewModel.getMovieCast(movieId)
    }

    val movieDetails = detailsViewModel.movieDetails.collectAsState().value
    val movieCast = detailsViewModel.movieCast.collectAsState().value
    val similarMovies = detailsViewModel.similarMovies.collectAsState().value
    val movieVideo = detailsViewModel.movieVideo.collectAsState().value
    val isMovieFavorite = detailsViewModel.movieIsFavorite.collectAsState().value

    val context = LocalContext.current

    val scrollState = rememberScrollState()
    val collapsingScrollState = rememberCollapsingToolbarScaffoldState()

    CollapsingToolbarScaffold(
        modifier = Modifier.fillMaxSize(),
        state = collapsingScrollState,
        scrollStrategy = ScrollStrategy.ExitUntilCollapsed,
        toolbar = {
            DetailsAppBar(
                modifier=Modifier.fillMaxWidth(),
                collapsingScrollState=collapsingScrollState,
                movieDetails = movieDetails,
                onNavigationIconClick = { /*TODO*/ },
                onShareIconClick = { /*TODO*/ }
            ) {
            }
        }
    ) {

        Column(
            modifier = Modifier
                .padding(bottom = 20.dp)
                .verticalScroll(state = scrollState),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {

            //region Movie Ratings
            val voteAverage = movieDetails?.voteAverage
            MovieRatingSection(
                popularity = voteAverage?.getPopularity(),
                voteAverage = voteAverage?.getRating()
            )
            //endregion

            //region Movie Overview
            Text(
                modifier = Modifier.padding(horizontal = 16.dp),
                text = stringResource(R.string.overview),
                style = MaterialTheme.typography.h6,
                fontSize = 20.sp,
                color = MaterialTheme.colors.onSurface,
            )

            Text(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .placeholder(
                        visible = movieDetails?.overview.isNullOrEmpty(),
                        color = Gray,
                        highlight = PlaceholderHighlight.fade(highlightColor = Color.Gray)
                    ),
                text = movieDetails?.overview ?: "",
                style = MaterialTheme.typography.body1,
                color = MaterialTheme.colors.onSurface,
                fontSize = 15.sp,
                textAlign = TextAlign.Start,
                overflow = TextOverflow.Ellipsis,
            )
            //endregion

            //region Movie Cast
            movieCast?.actor?.let {
                Text(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    text = stringResource(id = R.string.cast),
                    style = MaterialTheme.typography.h6,
                    fontSize = 20.sp
                )

                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(items = it) { item ->
                        ItemMovieCast(actor = item)
                    }
                }
            }
            //endregion

            //region Similar Movies
            Text(
                modifier = Modifier.padding(horizontal = 16.dp),
                text = stringResource(id = R.string.similar_movies),
                style = MaterialTheme.typography.h6,
                fontSize = 20.sp,
                color = MaterialTheme.colors.onSurface
            )

            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                similarMovies?.let {
                    items(items = it) { movie ->
                        ItemSimilarMovies(movie = movie)
                    }
                }
            }
            //endregion
        }
    }

    /*Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colors.surface
    ) {
        Box {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 20.dp)
                    .verticalScroll(state = scrollState),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {

                //region Movie Poster
                MoviePoster(
                    modifier = Modifier,
                    movieDetails = movieDetails
                )
                //endregion

                //region Movie Ratings
                val voteAverage = movieDetails?.voteAverage
                MovieRatingSection(
                    popularity = voteAverage?.getPopularity(),
                    voteAverage = voteAverage?.getRating()
                )
                //endregion

                //region Movie Overview
                Text(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    text = stringResource(R.string.overview),
                    style = MaterialTheme.typography.h6,
                    fontSize = 20.sp,
                    color = MaterialTheme.colors.onSurface,
                )

                Text(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .placeholder(
                            visible = movieDetails?.overview.isNullOrEmpty(),
                            color = Gray,
                            highlight = PlaceholderHighlight.fade(highlightColor = Color.Gray)
                        ),
                    text = movieDetails?.overview ?: "",
                    style = MaterialTheme.typography.body1,
                    color = MaterialTheme.colors.onSurface,
                    fontSize = 15.sp,
                    textAlign = TextAlign.Start,
                    overflow = TextOverflow.Ellipsis,
                )
                //endregion

                //region Movie Cast
                movieCast?.actor?.let {
                    Text(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        text = stringResource(id = R.string.cast),
                        style = MaterialTheme.typography.h6,
                        fontSize = 20.sp
                    )

                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(items = it) { item ->
                            ItemMovieCast(actor = item)
                        }
                    }
                }
                //endregion

                //region Similar Movies
                Text(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    text = stringResource(id = R.string.similar_movies),
                    style = MaterialTheme.typography.h6,
                    fontSize = 20.sp,
                    color = MaterialTheme.colors.onSurface
                )

                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    similarMovies?.let {
                        items(items = it) { movie ->
                            ItemSimilarMovies(movie = movie)
                        }
                    }
                }
                //endregion
            }
        }
    }*/
}

private fun shareMovie(context: Context, movieId: Int) {
    val shareIntent = Intent()
    shareIntent.action = Intent.ACTION_SEND
    shareIntent.type = "text/plain"
    shareIntent.putExtra(Intent.EXTRA_TEXT, "https://com.vickikbt.notlfix/id?=$movieId")
    context.startActivity(
        Intent.createChooser(
            shareIntent,
            context.resources.getString(R.string.share)
        )
    )
}
