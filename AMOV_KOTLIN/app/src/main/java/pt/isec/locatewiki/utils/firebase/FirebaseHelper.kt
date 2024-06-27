package pt.isec.locatewiki.utils.firebase

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import pt.isec.locatewiki.ui.data.Category
import pt.isec.locatewiki.ui.data.Landmark
import pt.isec.locatewiki.ui.data.Location


object FirebaseHelper {
    private val firebaseStorage by lazy { FirebaseStorage.getInstance() }
    private val firebaseAuth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    private val firebaseFirestore: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }

    private fun <T> handleTaskResult(task: Task<T>, responseHandler: FirebaseResponseHandler<T>) {
        task.addOnSuccessListener { responseHandler.onSuccess(it) }
            .addOnFailureListener { responseHandler.onFailure(it) }
    }

    // Firebase Authentication Methods
    //
    fun getCurrentUser(): FirebaseUser? {
        return firebaseAuth.currentUser
    }

    fun signIn(
        email: String,
        password: String,
        responseHandler: FirebaseResponseHandler<AuthResult>
    ) {
        handleTaskResult(firebaseAuth.signInWithEmailAndPassword(email, password), responseHandler)
    }

    fun createUser(
        email: String,
        password: String,
        responseHandler: FirebaseResponseHandler<AuthResult>
    ) {
        handleTaskResult(
            firebaseAuth.createUserWithEmailAndPassword(email, password),
            responseHandler
        )
    }

    fun signOut() {
        firebaseAuth.signOut()
    }

    // Firebase Firestore Methods
    //
    fun getLocations(responseHandler: FirebaseResponseHandler<Set<Location>>) {
        firebaseFirestore.collection("locations").get().addOnSuccessListener { querySnapshot ->
            val locations = mutableSetOf<Location>()
            querySnapshot.forEach { documentSnapshot ->
                documentSnapshot.toObject<Location>().let { locations.add(it) }
            }
            responseHandler.onSuccess(locations)
        }.addOnFailureListener { responseHandler.onFailure(it) }
    }

    fun addLocation(location: Location, responseHandler: FirebaseResponseHandler<Location>) {
        handleTaskResult(firebaseFirestore.collection("locations").add(location),
            FirebaseResponseHandler(
                successCallback =
                {
                    location.id = it!!.id
                    updateLocation(location, FirebaseResponseHandler(
                        successCallback =
                        {
                            responseHandler.onSuccess(location)
                        },
                        failureCallback =
                        {
                            responseHandler.onFailure(it)
                        }
                    ))
                },
                failureCallback =
                {
                    responseHandler.onFailure(it)
                }
            ))
    }

    fun updateLocation(location: Location, responseHandler: FirebaseResponseHandler<Void>) {
        location.id?.let {
            handleTaskResult(
                firebaseFirestore.collection("locations").document(it).set(location),
                responseHandler
            )
        } ?: responseHandler.onFailure(Exception("Location ID is null"))
    }

    fun deleteLocation(location: Location, responseHandler: FirebaseResponseHandler<Void>) {
        location.id?.let {
            handleTaskResult(
                firebaseFirestore.collection("locations").document(it).delete(),
                responseHandler
            )
        } ?: responseHandler.onFailure(Exception("Location ID is null"))
    }

    fun observeLocations(responseHandler: FirebaseResponseHandler<Set<Location>>) {
        firebaseFirestore.collection("locations").addSnapshotListener { querySnapshot, exception ->
            if (exception != null)
                responseHandler.onFailure(exception)
            else {
                val locations = mutableSetOf<Location>()
                querySnapshot?.forEach { documentSnapshot ->
                    documentSnapshot.toObject<Location>().let { locations.add(it) }
                }
                responseHandler.onSuccess(locations)
            }
        }
    }

    fun getCategories(responseHandler: FirebaseResponseHandler<Set<Category>>) {
        firebaseFirestore.collection("categories").get().addOnSuccessListener { querySnapshot ->
            val categories = mutableSetOf<Category>()
            querySnapshot.forEach { documentSnapshot ->
                documentSnapshot.toObject<Category>().let { categories.add(it) }
            }
            responseHandler.onSuccess(categories)
        }.addOnFailureListener { responseHandler.onFailure(it) }
    }

    fun addCategory(category: Category, responseHandler: FirebaseResponseHandler<Category>) {
        handleTaskResult(firebaseFirestore.collection("categories").add(category),
            FirebaseResponseHandler(
                successCallback =
                {
                    category.id = it!!.id
                    updateCategory(category, FirebaseResponseHandler(
                        successCallback =
                        {
                            responseHandler.onSuccess(category)
                        },
                        failureCallback =
                        {
                            responseHandler.onFailure(it)
                        }
                    ))
                },
                failureCallback =
                {
                    responseHandler.onFailure(it)
                }

            ))
    }

    fun updateCategory(category: Category, responseHandler: FirebaseResponseHandler<Void>) {
        category.id?.let {
            handleTaskResult(
                firebaseFirestore.collection("categories").document(it).set(category),
                responseHandler
            )
        } ?: responseHandler.onFailure(Exception("pt.isec.locatewiki.data.Category ID is null"))
    }

    fun deleteCategory(category: Category, responseHandler: FirebaseResponseHandler<Void>) {
        category.id?.let {
            handleTaskResult(
                firebaseFirestore.collection("categories").document(it).delete(),
                responseHandler
            )
        } ?: responseHandler.onFailure(Exception("pt.isec.locatewiki.data.Category ID is null"))
    }

    fun observeCategories(responseHandler: FirebaseResponseHandler<Set<Category>>) {
        firebaseFirestore.collection("categories").addSnapshotListener { querySnapshot, exception ->
            if (exception != null)
                responseHandler.onFailure(exception)
            else {
                val categories = mutableSetOf<Category>()
                querySnapshot?.forEach { documentSnapshot ->
                    documentSnapshot.toObject<Category>().let { categories.add(it) }
                }
                responseHandler.onSuccess(categories)
            }
        }
    }

    fun getLandmarks(responseHandler: FirebaseResponseHandler<Set<Landmark>>) {
        firebaseFirestore.collectionGroup("landmarks").get().addOnSuccessListener { querySnapshot ->
            val landmarks = mutableSetOf<Landmark>()
            querySnapshot.forEach { documentSnapshot ->
                documentSnapshot.toObject<Landmark>().let { landmarks.add(it) }
            }
            responseHandler.onSuccess(landmarks)
        }.addOnFailureListener { responseHandler.onFailure(it) }
    }

    fun addLandmark(landmark: Landmark, responseHandler: FirebaseResponseHandler<Landmark>) {
        handleTaskResult(firebaseFirestore.collection("locations").document(landmark.locationId)
            .collection("landmarks").add(landmark), FirebaseResponseHandler(
            successCallback =
            {
                landmark.id = it!!.id
                updateLandmark(landmark, FirebaseResponseHandler(
                    successCallback =
                    {
                        responseHandler.onSuccess(landmark)
                    },
                    failureCallback =
                    {
                        responseHandler.onFailure(it)
                    }
                ))
            },
            failureCallback =
            {
                responseHandler.onFailure(it)
            }

        ))
    }

    fun updateLandmark(landmark: Landmark, responseHandler: FirebaseResponseHandler<Void>) {
        landmark.id?.let {
            handleTaskResult(
                firebaseFirestore.collection("locations").document(landmark.locationId)
                    .collection("landmarks").document(it).set(landmark), responseHandler
            )
        } ?: responseHandler.onFailure(Exception("pt.isec.locatewiki.data.Landmark ID is null"))
    }

    fun deleteLandmark(landmark: Landmark, responseHandler: FirebaseResponseHandler<Void>) {
        landmark.id?.let {
            handleTaskResult(
                firebaseFirestore.collection("locations").document(landmark.locationId)
                    .collection("landmarks").document(it).delete(), responseHandler
            )
        } ?: responseHandler.onFailure(Exception("pt.isec.locatewiki.data.Landmark ID is null"))
    }

    fun observeLandmarks(responseHandler: FirebaseResponseHandler<Set<Landmark>>) {
        firebaseFirestore.collectionGroup("landmarks")
            .addSnapshotListener { querySnapshot, exception ->
                if (exception != null)
                    responseHandler.onFailure(exception)
                else {
                    val landmarks = mutableSetOf<Landmark>()
                    querySnapshot?.forEach { documentSnapshot ->
                        documentSnapshot.toObject<Landmark>().let { landmarks.add(it) }
                    }
                    responseHandler.onSuccess(landmarks)
                }
            }
    }

    // Firebase Storage Methods
    //
    fun getImage(path: String, responseHandler: FirebaseResponseHandler<ByteArray>) {
        val storageReference = firebaseStorage.reference.child(path)
        storageReference.getBytes(1024 * 1024)
            .addOnSuccessListener { responseHandler.onSuccess(it) }
            .addOnFailureListener { responseHandler.onFailure(it) }
    }

    fun uploadImage(
        path: String,
        image: ByteArray,
        responseHandler: FirebaseResponseHandler<UploadTask.TaskSnapshot>
    ) {
        if (image.size > 1024 * 1024)
            responseHandler.onFailure(Exception("Image size is too big"))

        val storageReference = firebaseStorage.reference
        val imageReference = storageReference.child(path)
        val uploadTask = imageReference.putBytes(image)
        uploadTask.addOnSuccessListener { responseHandler.onSuccess(it) }
            .addOnFailureListener { responseHandler.onFailure(it) }
    }

    fun deleteImage(path: String, responseHandler: FirebaseResponseHandler<Void>) {
        val storageReference = firebaseStorage.reference
        val imageReference = storageReference.child(path)
        imageReference.delete().addOnSuccessListener { responseHandler.onSuccess(null) }
            .addOnFailureListener { responseHandler.onFailure(it) }
    }
}