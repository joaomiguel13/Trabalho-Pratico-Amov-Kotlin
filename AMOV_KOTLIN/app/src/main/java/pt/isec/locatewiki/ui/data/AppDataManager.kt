package pt.isec.locatewiki.ui.data

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY
import com.google.firebase.firestore.GeoPoint
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import pt.isec.locatewiki.MainActivity
import pt.isec.locatewiki.ui.states.AppDataState
import pt.isec.locatewiki.utils.firebase.FirebaseHelper
import pt.isec.locatewiki.utils.firebase.FirebaseResponseHandler
import java.io.ByteArrayOutputStream

object AppDataManager {
    private val _state = MutableStateFlow(AppDataState())
    val state: StateFlow<AppDataState> = _state

    init {
        FirebaseHelper.getLocations(
            FirebaseResponseHandler(
                successCallback =
                {
                    _state.value = _state.value.copy(dbLocations = it!!.toMutableSet())
                },
                failureCallback =
                {
                    _state.value = _state.value.copy(error = it)
                }
            )
        )

        FirebaseHelper.observeLocations(
            FirebaseResponseHandler(
                successCallback =
                {
                    _state.value = _state.value.copy(dbLocations = it!!.toMutableSet())
                },
                failureCallback =
                {
                    _state.value = _state.value.copy(error = it)
                }
            )
        )

        FirebaseHelper.getLandmarks(
            FirebaseResponseHandler(
                successCallback =
                {
                    _state.value = _state.value.copy(dbLandmarks = it!!.toMutableSet())
                },
                failureCallback =
                {
                    _state.value = _state.value.copy(error = it)
                }
            )
        )

        FirebaseHelper.observeLandmarks(
            FirebaseResponseHandler(
                successCallback =
                {
                    _state.value = _state.value.copy(dbLandmarks = it!!.toMutableSet())
                },
                failureCallback =
                {
                    _state.value = _state.value.copy(error = it)
                }
            )
        )

        FirebaseHelper.getCategories(
            FirebaseResponseHandler(
                successCallback =
                {
                    _state.value = _state.value.copy(dbCategories = it!!.toMutableSet())
                },
                failureCallback =
                {
                    _state.value = _state.value.copy(error = it)
                }
            )
        )

        FirebaseHelper.observeCategories(
            FirebaseResponseHandler(
                successCallback =
                {
                    _state.value = _state.value.copy(dbCategories = it!!.toMutableSet())
                },
                failureCallback =
                {
                    _state.value = _state.value.copy(error = it)
                }
            )
        )
    }

    fun createLocation(title: String, geoPoint: GeoPoint, otherInfo: String) {
        val location = Location(title, geoPoint, otherInfo, FirebaseHelper.getCurrentUser()!!.uid)

        FirebaseHelper.addLocation(
            location,
            FirebaseResponseHandler(
                successCallback =
                {
                    _state.value =
                        _state.value.copy(dbLocations = _state.value.dbLocations.apply { add(it!!) })
                },
                failureCallback =
                {
                    _state.value = _state.value.copy(error = it)
                }
            )
        )
    }

    fun deleteLocation(location: Location) {
        FirebaseHelper.deleteLocation(
            location,
            FirebaseResponseHandler(
                successCallback =
                {
                    _state.value = _state.value.copy(dbLocations = _state.value.dbLocations.apply {
                        remove(location)
                    })
                },
                failureCallback =
                {
                    _state.value = _state.value.copy(error = it)
                }
            )
        )
    }

    fun updateLocation(locationId: String, title: String, otherInfo: String) {
        val oldLocation = getLocationById(locationId)!!
        val location = oldLocation.copy(
            title = title,
            otherInfo = otherInfo,
            usersTrust = listOf(oldLocation.ownerId)
        )

        FirebaseHelper.updateLocation(
            location,
            FirebaseResponseHandler(
                successCallback =
                {
                    _state.value = _state.value.copy(dbLocations = _state.value.dbLocations.apply {
                        removeIf { it.id == locationId }
                        add(location)
                    })
                },
                failureCallback =
                {
                    _state.value = _state.value.copy(error = it)
                }
            )
        )
    }

    fun approveLocation(location: Location) {
        if (location.usersTrust.contains(FirebaseHelper.getCurrentUser()!!.uid))
            return

        val newLocation =
            location.copy(usersTrust = location.usersTrust + FirebaseHelper.getCurrentUser()!!.uid)

        FirebaseHelper.updateLocation(
            newLocation,
            FirebaseResponseHandler(
                successCallback =
                {
                    _state.value = _state.value.copy(dbLocations = _state.value.dbLocations.apply {
                        removeIf { it.id == location.id }
                        add(newLocation)
                    })
                },
                failureCallback =
                {
                    _state.value = _state.value.copy(error = it)
                }
            )
        )
    }

    fun getLocationById(id: String): Location? {
        return _state.value.dbLocations.find { it.id == id }
    }

    fun createCategory(name: String, icon: CategoryIcon) {
        val category = Category(name, icon, FirebaseHelper.getCurrentUser()!!.uid)

        FirebaseHelper.addCategory(
            category,
            FirebaseResponseHandler(
                successCallback =
                {
                    _state.value =
                        _state.value.copy(dbCategories = _state.value.dbCategories.apply { add(it!!) })
                },
                failureCallback =
                {
                    _state.value = _state.value.copy(error = it)
                }
            )
        )
    }

    fun deleteCategory(category: Category) {
        FirebaseHelper.deleteCategory(
            category,
            FirebaseResponseHandler(
                successCallback =
                {
                    _state.value =
                        _state.value.copy(dbCategories = _state.value.dbCategories.apply {
                            remove(category)
                        })
                },
                failureCallback =
                {
                    _state.value = _state.value.copy(error = it)
                }
            )
        )
    }

    fun updateCategory(categoryId: String, icon: CategoryIcon, name: String) {
        val oldCategory = getCategoryById(categoryId)!!
        val category =
            oldCategory.copy(name = name, icon = icon, usersTrust = listOf(oldCategory.ownerId))

        FirebaseHelper.updateCategory(
            category,
            FirebaseResponseHandler(
                successCallback =
                {
                    _state.value =
                        _state.value.copy(dbCategories = _state.value.dbCategories.apply {
                            removeIf { it.id == categoryId }
                            add(category)
                        })
                },
                failureCallback =
                {
                    _state.value = _state.value.copy(error = it)
                }
            )
        )
    }

    fun approveCategory(category: Category) {
        if (category.usersTrust.contains(FirebaseHelper.getCurrentUser()!!.uid))
            return

        val newCategory =
            category.copy(usersTrust = category.usersTrust + FirebaseHelper.getCurrentUser()!!.uid)

        FirebaseHelper.updateCategory(
            newCategory,
            FirebaseResponseHandler(
                successCallback =
                {
                    _state.value =
                        _state.value.copy(dbCategories = _state.value.dbCategories.apply {
                            removeIf { it.id == category.id }
                            add(newCategory)
                        })
                },
                failureCallback =
                {
                    _state.value = _state.value.copy(error = it)
                }
            )
        )
    }

    fun getCategoryById(id: String): Category? {
        return _state.value.dbCategories.find { it.id == id }
    }

    fun createLandmark(
        context: Context,
        locationId: String,
        categoryId: String,
        title: String,
        geoPoint: GeoPoint,
        otherInfo: String,
        selectedImage: Uri
    ) {
        val landmark = Landmark(
            locationId,
            categoryId,
            title,
            geoPoint,
            otherInfo,
            FirebaseHelper.getCurrentUser()!!.uid
        )

        FirebaseHelper.addLandmark(
            landmark,
            FirebaseResponseHandler(
                successCallback =
                {
                    val bitmap = BitmapFactory.decodeStream(
                        context.contentResolver.openInputStream(selectedImage)
                    )

                    val aspectRatio = bitmap.width.toFloat() / bitmap.height.toFloat()

                    val newWidth = 400
                    val newHeight = (400 / aspectRatio).toInt()

                    val resizedBitmap = Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)

                    val stream = ByteArrayOutputStream()
                    resizedBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
                    val byteArray = stream.toByteArray()

                    FirebaseHelper.uploadImage(
                        "landmarkImages/${it!!.locationId}/${it.id}.png",
                        byteArray,
                        FirebaseResponseHandler(
                            successCallback =
                            { _ ->
                                _state.value =
                                    _state.value.copy(dbLandmarks = _state.value.dbLandmarks.apply {
                                        add(it)
                                    })

                                Log.d("UploadImage", "Image uploaded successfully")
                            },
                            failureCallback =
                            { e ->
                                _state.value =
                                    _state.value.copy(dbLandmarks = _state.value.dbLandmarks.apply {
                                        add(it)
                                    }, error = e)

                                Log.d("UploadImage", "Image upload failed")
                            }
                        )
                    )
                },
                failureCallback =
                {
                    _state.value = _state.value.copy(error = it)
                }
            )
        )
    }

    fun getLandmarkImage(landmark: Landmark, onSuccess: (Bitmap) -> Unit) {
        FirebaseHelper.getImage(
            "landmarkImages/${landmark.locationId}/${landmark.id}.png",
            FirebaseResponseHandler(
                successCallback =
                {
                    val bitmap = BitmapFactory.decodeByteArray(it, 0, it!!.size)
                    onSuccess(bitmap)
                    Log.d("GetImage", "Image downloaded successfully")
                },
                failureCallback =
                {
                    Log.d("GetImage", "Image download failed")
                }
            ))
    }

    fun createClassification(
        context: Context,
        landmark: Landmark,
        classification: Int,
        comment: String,
        selectedImage: Uri
    )
    {
        val newClassification = landmark.usersClassification.toMutableMap()
        newClassification[FirebaseHelper.getCurrentUser()!!.uid] = classification

        val newComment = landmark.usersComment.toMutableMap()
        newComment[FirebaseHelper.getCurrentUser()!!.uid] = comment

        val newLandmark = landmark.copy(usersClassification = newClassification, usersComment = newComment)

        FirebaseHelper.updateLandmark(
            newLandmark,
            FirebaseResponseHandler(
                successCallback =
                {
                    val bitmap = BitmapFactory.decodeStream(
                        context.contentResolver.openInputStream(selectedImage)
                    )

                    val aspectRatio = bitmap.width.toFloat() / bitmap.height.toFloat()

                    val newWidth = 400
                    val newHeight = (400 / aspectRatio).toInt()

                    val resizedBitmap = Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)

                    val stream = ByteArrayOutputStream()
                    resizedBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
                    val byteArray = stream.toByteArray()

                    FirebaseHelper.uploadImage(
                        "commentImages/${landmark.locationId}/${landmark.id}/${FirebaseHelper.getCurrentUser()!!.uid}.png",
                        byteArray,
                        FirebaseResponseHandler(
                            successCallback =
                            { _ ->
                                _state.value =
                                    _state.value.copy(dbLandmarks = _state.value.dbLandmarks.apply {
                                        removeIf { it.id == landmark.id }
                                        add(newLandmark)
                                    })

                                Log.d("UploadImage", "Image uploaded successfully")
                            },
                            failureCallback =
                            { e ->
                                _state.value =
                                    _state.value.copy(dbLandmarks = _state.value.dbLandmarks.apply {
                                        removeIf { it.id == landmark.id }
                                        add(newLandmark)
                                    }, error = e)

                                Log.d("UploadImage", "Image upload failed")
                            }
                        )
                    )
                },
                failureCallback =
                {
                    _state.value = _state.value.copy(error = it)
                }
            )
        )
    }

    fun getClassificationImage(landmark: Landmark, userId: String, onSuccess: (Bitmap) -> Unit) {
        Log.d("getClassificationImage", "Downloading image")
        FirebaseHelper.getImage(
            "commentImages/${landmark.locationId}/${landmark.id}/${userId}.png",
            FirebaseResponseHandler(
                successCallback =
                {
                    val bitmap = BitmapFactory.decodeByteArray(it, 0, it!!.size)
                    onSuccess(bitmap)
                    Log.d("GetImage", "Image downloaded successfully")
                },
                failureCallback =
                {
                    Log.d("GetImage", "Image download failed")
                }
            ))
    }

    fun deleteLandmark(landmark: Landmark) {
        if (!landmark.beingDeleted && landmark.usersTrust.isNotEmpty()) {
            val newLandmark = landmark.copy(beingDeleted = true, usersTrust = listOf(FirebaseHelper.getCurrentUser()!!.uid))
            return FirebaseHelper.updateLandmark(newLandmark, FirebaseResponseHandler(
                {
                    _state.value = _state.value.copy(dbLandmarks = _state.value.dbLandmarks.apply {
                        removeIf { it.id == newLandmark.id }
                        add(newLandmark)
                    })
                },
                {
                    _state.value = _state.value.copy(error = it)
                }
            ))
        }

        FirebaseHelper.deleteLandmark(
            landmark,
            FirebaseResponseHandler(
                successCallback =
                {
                    _state.value = _state.value.copy(dbLandmarks = _state.value.dbLandmarks.apply {
                        remove(landmark)
                    })
                    FirebaseHelper.deleteImage(
                        "landmarkImages/${landmark.locationId}/${landmark.id}.png",
                        FirebaseResponseHandler(
                            successCallback =
                            {
                                Log.d("DeleteImage", "Image deleted successfully")
                            },
                            failureCallback =
                            {
                                Log.d("DeleteImage", "Image delete failed")
                            }
                        ))
                    FirebaseHelper.deleteImage(
                        "commentImages/${landmark.locationId}/${landmark.id}",
                        FirebaseResponseHandler(
                            successCallback =
                            {
                                Log.d("DeleteImage", "Image deleted successfully")
                            },
                            failureCallback =
                            {
                                Log.d("DeleteImage", "Image delete failed")
                            }
                        )
                    )
                },
                failureCallback =
                {
                    _state.value = _state.value.copy(error = it)
                }
            )
        )
    }

    fun updateLandmark(landmarkId: String, categoryId: String, title: String, otherInfo: String) {
        val oldLandmark = getLandmarkById(landmarkId)!!
        val landmark = oldLandmark.copy(
            categoryId = categoryId,
            title = title,
            otherInfo = otherInfo,
            usersTrust = listOf(oldLandmark.ownerId)
        )

        FirebaseHelper.updateLandmark(
            landmark,
            FirebaseResponseHandler(
                successCallback =
                {
                    _state.value = _state.value.copy(dbLandmarks = _state.value.dbLandmarks.apply {
                        removeIf { it.id == landmark.id }
                        add(landmark)
                    })
                },
                failureCallback =
                {
                    _state.value = _state.value.copy(error = it)
                }
            )
        )
    }

    fun approveLandmark(landmark: Landmark) {
        if (landmark.usersTrust.contains(FirebaseHelper.getCurrentUser()!!.uid))
            return

        if (landmark.beingDeleted && landmark.usersTrust.size >= 2)
            return deleteLandmark(landmark)

        val newLandmark =
            landmark.copy(usersTrust = landmark.usersTrust + FirebaseHelper.getCurrentUser()!!.uid)

        FirebaseHelper.updateLandmark(
            newLandmark,
            FirebaseResponseHandler(
                successCallback =
                {
                    _state.value = _state.value.copy(dbLandmarks = _state.value.dbLandmarks.apply {
                        removeIf { it.id == landmark.id }
                        add(newLandmark)
                    })
                },
                failureCallback =
                {
                    _state.value = _state.value.copy(error = it)
                }
            )
        )
    }

    fun getLandmarkById(id: String): Landmark? {
        return _state.value.dbLandmarks.find { it.id == id }
    }

    // Location related methods
    //
    private val locationProvider: FusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(MainActivity.instance)
    }

    private var coarseLocationPermission = false
    private var fineLocationPermission = false
    private var backgroundLocationPermission = false
    private var locationEnabled = false

    fun verifyPermissions() {
        coarseLocationPermission = ContextCompat.checkSelfPermission(
            MainActivity.instance,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        fineLocationPermission = ContextCompat.checkSelfPermission(
            MainActivity.instance,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            backgroundLocationPermission = ContextCompat.checkSelfPermission(
                MainActivity.instance,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        } else
            backgroundLocationPermission = coarseLocationPermission || fineLocationPermission

        if (!coarseLocationPermission && !fineLocationPermission) {
            basicPermissionsAuthorization.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        } else
            verifyBackgroundPermission()
    }

    private val basicPermissionsAuthorization = MainActivity.instance.registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { results ->
        coarseLocationPermission = results[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false
        fineLocationPermission = results[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
        startLocationUpdates()
        verifyBackgroundPermission()
    }

    private fun verifyBackgroundPermission() {
        if (!(coarseLocationPermission || fineLocationPermission))
            return

        if (!backgroundLocationPermission && Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    MainActivity.instance, Manifest.permission.ACCESS_BACKGROUND_LOCATION
                )
            ) {
                val dlg = AlertDialog.Builder(MainActivity.instance)
                    .setTitle("Background Location")
                    .setMessage("Background location is needed to get location updates while the app is in the background.")
                    .setPositiveButton("Ok") { _, _ ->
                        backgroundPermissionAuthorization.launch(
                            Manifest.permission.ACCESS_BACKGROUND_LOCATION
                        )
                    }
                    .create()
                dlg.show()
            }
        }
    }

    private val backgroundPermissionAuthorization = MainActivity.instance.registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { result ->
        backgroundLocationPermission = result
        Toast.makeText(
            MainActivity.instance,
            "Background location enabled: $result",
            Toast.LENGTH_LONG
        ).show()
    }

    @SuppressLint("MissingPermission")
    fun startLocationUpdates() {
        locationProvider.lastLocation
            .addOnSuccessListener { location ->
                if (location == null) return@addOnSuccessListener

                _state.value = _state.value.copy(currentLocation = location)
            }

        val locationRequest = LocationRequest.Builder(PRIORITY_HIGH_ACCURACY, 2000)
            .build()
        locationProvider.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )

        locationEnabled = true
    }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult) {
            super.onLocationResult(result)
            result.locations.forEach { location ->
                _state.value =
                    _state.value.copy(currentLocation = android.location.Location(null).apply {
                        latitude = location.latitude
                        longitude = location.longitude
                    })
            }
        }
    }

    fun stopLocationUpdates() {
        if (!locationEnabled)
            return
        locationProvider.removeLocationUpdates(locationCallback)
        locationEnabled = false
    }
}