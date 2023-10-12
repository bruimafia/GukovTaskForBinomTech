package ru.bruimafia.gukovtaskforbinomtech

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.preference.PreferenceManager
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.floatingactionbutton.FloatingActionButton
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.ScaleBarOverlay
import org.osmdroid.views.overlay.compass.CompassOverlay
import org.osmdroid.views.overlay.compass.InternalCompassOrientationProvider
import org.osmdroid.views.overlay.gestures.RotationGestureOverlay
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay


class MainActivity : AppCompatActivity() {

    companion object {
        private const val MAP_DEFAULT_ZOOM = 15.0
        private const val MAP_MARKER_ZOOM = 17.0
        private const val START_POINT_LATITUDE = 56.0
        private const val START_POINT_LONGITUDE = 30.0
        private const val ACCESS_FINE_LOCATION = 101
    }

    private lateinit var map: MapView
    private lateinit var locationOverlay: MyLocationNewOverlay
    private var currentMarker = 0
    private val markers = ArrayList<Marker>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Configuration.getInstance().load(applicationContext, PreferenceManager.getDefaultSharedPreferences(applicationContext))
        setContentView(R.layout.activity_main)

        checkPermission(Manifest.permission.ACCESS_FINE_LOCATION, ACCESS_FINE_LOCATION)

        map = findViewById(R.id.map)
        map.setTileSource(TileSourceFactory.MAPNIK)
        map.setBuiltInZoomControls(false)
        map.setMultiTouchControls(true)
        map.controller.setZoom(MAP_DEFAULT_ZOOM)
        map.controller.setCenter(GeoPoint(START_POINT_LATITUDE, START_POINT_LONGITUDE))

        // наложение моего местоположения
        locationOverlay = MyLocationNewOverlay(GpsMyLocationProvider(applicationContext), map)
        val currentDraw = ResourcesCompat.getDrawable(resources, R.drawable.ic_my_tracker_46dp, null)
        var currentIcon: Bitmap? = null
        if (currentDraw != null) {
            currentIcon = (currentDraw as BitmapDrawable).bitmap
        }
        locationOverlay.setPersonIcon(currentIcon)
        locationOverlay.enableMyLocation()
        map.overlays.add(locationOverlay)

        // наложение компаса
        val compassOverlay = CompassOverlay(applicationContext, InternalCompassOrientationProvider(applicationContext), map)
        compassOverlay.enableCompass()
        map.overlays.add(compassOverlay)

        // включение жестов поворота
        val rotationGestureOverlay = RotationGestureOverlay(map)
        rotationGestureOverlay.isEnabled = true
        map.overlays.add(rotationGestureOverlay)

        // наложение масштабной линейки карты
        val dm = applicationContext.resources.displayMetrics
        val scaleBarOverlay = ScaleBarOverlay(map)
        scaleBarOverlay.setCentred(true)
        scaleBarOverlay.setScaleBarOffset(dm.widthPixels / 2, 20)
        map.overlays.add(scaleBarOverlay)

        // наложение встроенной миникарты
//        val minimapOverlay = MinimapOverlay(applicationContext, map.tileRequestCompleteHandler)
//        minimapOverlay.width = dm.widthPixels / 4
//        minimapOverlay.height = dm.heightPixels / 5
//        map.overlays.add(minimapOverlay)

        // установка маркеров
        setupMarkers()

        // установка слушателей для кнопок управления
        setButtonsListeners()
    }

    private fun setButtonsListeners() {
        val btn_zoomIn = findViewById<FloatingActionButton>(R.id.btn_zoomIn)
        btn_zoomIn.setOnClickListener {
            map.controller.zoomIn()
        }

        val btn_zoomOut = findViewById<FloatingActionButton>(R.id.btn_zoomOut)
        btn_zoomOut.setOnClickListener {
            map.controller.zoomOut()
        }

        val btn_myLocation = findViewById<FloatingActionButton>(R.id.btn_myLocation)
        btn_myLocation.setOnClickListener {
            map.controller.animateTo(locationOverlay.myLocation)
            map.controller.setZoom(MAP_MARKER_ZOOM)
        }

        val btn_nextTracker = findViewById<FloatingActionButton>(R.id.btn_nextTracker)
        btn_nextTracker.setOnClickListener {
            map.controller.animateTo(markers[currentMarker].position)
            map.controller.setZoom(MAP_MARKER_ZOOM)
            currentMarker++
            if (currentMarker >= markers.size)
                currentMarker = currentMarker.mod(markers.size)
        }
    }

    private fun setupMarkers() {
        val marker1 = Marker(map)
        with(marker1) {
            position = GeoPoint(59.974554, 30.283933)
            icon = ContextCompat.getDrawable(applicationContext, R.drawable.ic_house)
            image = ContextCompat.getDrawable(applicationContext, R.drawable.img_house)
            title = "Дом"
            snippet = "набережная реки Малой Невки, 35А, Санкт-Петербург, 197022"
            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
        }
        markers.add(marker1)

        val marker2 = Marker(map)
        with(marker2) {
            position = GeoPoint(59.987111, 30.178101)
            icon = ContextCompat.getDrawable(applicationContext, R.drawable.ic_job)
            image = ContextCompat.getDrawable(applicationContext, R.drawable.img_job)
            title = "Работа (Лахта центр)"
            snippet = "Высотная улица, 1, Санкт-Петербург"
            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
        }
        markers.add(marker2)

        val marker3 = Marker(map)
        with(marker3) {
            position = GeoPoint(59.924870, 30.319139)
            icon = ContextCompat.getDrawable(applicationContext, R.drawable.ic_market)
            image = ContextCompat.getDrawable(applicationContext, R.drawable.img_market)
            title = "Сенной рынок"
            snippet = "Московский проспект, 4АБ, Санкт-Петербург, 190031"
            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
        }
        markers.add(marker3)

        val marker4 = Marker(map)
        with(marker4) {
            position = GeoPoint(59.934018, 30.293809)
            icon = ContextCompat.getDrawable(applicationContext, R.drawable.ic_registry_office)
            image = ContextCompat.getDrawable(applicationContext, R.drawable.img_registry_office)
            title = "Дворец бракосочетания № 1"
            snippet = "Английская набережная, 28, Санкт-Петербург"
            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
        }
        markers.add(marker4)

        val marker5 = Marker(map)
        with(marker5) {
            position = GeoPoint(59.962513, 30.297997)
            icon = ContextCompat.getDrawable(applicationContext, R.drawable.ic_sport)
            image = ContextCompat.getDrawable(applicationContext, R.drawable.img_sport)
            title = "Спортивная площадка"
            snippet = "Гатчинская улица, 22, Санкт-Петербург, 197136"
            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
        }
        markers.add(marker5)

        for (marker in markers) {
            map.overlays.add(marker)
            marker.setOnMarkerClickListener { _, _ ->
                map.controller.animateTo(marker.position)
                map.controller.setZoom(MAP_MARKER_ZOOM)
                showMarkerInfo(marker)
                true
            }
        }
    }

    private fun showMarkerInfo(item: Marker) {
        val bottomSheetDialog = BottomSheetDialog(this)
        bottomSheetDialog.setContentView(R.layout.bottom_sheet_dialog)
        val tv_name = bottomSheetDialog.findViewById<TextView>(R.id.tv_name)
        val tv_desc = bottomSheetDialog.findViewById<TextView>(R.id.tv_desc)
        val iv_img = bottomSheetDialog.findViewById<ImageView>(R.id.iv_img)
        tv_name?.text = item.title
        tv_desc?.text = item.snippet
        iv_img?.setImageDrawable(item.image)
        bottomSheetDialog.show()
    }

    private fun checkPermission(permission: String, requestCode: Int) {
        if (ContextCompat.checkSelfPermission(this@MainActivity, permission) == PackageManager.PERMISSION_DENIED)
            ActivityCompat.requestPermissions(this@MainActivity, arrayOf(permission), requestCode)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == ACCESS_FINE_LOCATION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                Toast.makeText(this@MainActivity, "Предоставлено разрешение на определение местоположения", Toast.LENGTH_SHORT).show()
            else
                Toast.makeText(this@MainActivity, "Отказано в разрешении на определение местоположения", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onPause() {
        super.onPause()
        map.onPause()
    }

    override fun onResume() {
        super.onResume()
        map.onResume()
    }
}