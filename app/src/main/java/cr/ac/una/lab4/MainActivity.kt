package cr.ac.una.lab4

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {// onclick del tool
    lateinit var drawerLayout: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        drawerLayout = findViewById(R.id.drawer_layout)// recibe el id toolbar, son las 3
        // rayitas para desplegar el menu

        var toggle = ActionBarDrawerToggle(
            this,
            drawerLayout,
            toolbar,//se le pasa el toolbar
            R.string.navigation_drawer_open,//se le pasa el string de abrir y cerrar
            R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();//es el que se escucha/esconde cuando se abre y se cierra el menu

        val navigationView = findViewById<NavigationView>(R.id.navigation_view)
        navigationView.setNavigationItemSelectedListener(this)
    }

    override fun onBackPressed() { //cuando se presiona el boton de atras se cierra el menu
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onNavigationItemSelected(menuItem: MenuItem): Boolean {
        val title: Int
        lateinit var fragment: Fragment
        when (menuItem.getItemId()) {
            R.id.nav_camera -> {
                title = R.string.menu_camera
                //se pasa la actividad a un fragment
//                fragment = HomeFragment.newInstance(getString(title))
                fragment = HomeFragment()
            }
            R.id.nav_gallery -> {
                title = R.string.menu_gallery
                fragment= CameraFragment()
            }
            R.id.nav_manage -> title = R.string.menu_tools
            else -> throw IllegalArgumentException("menu option not implemented!!")
        }
        supportFragmentManager//se encarga de manejar los fragmentos
            .beginTransaction()
//.setCustomAnimations(R.anim.bottom_nav_enter, R.anim.bottom_nav_exit)
            .replace(R.id.home_content, fragment)//se reemplaza el fragmento actual por el nuevo
            .commit()
        setTitle(getString(title))//se cambia el titulo de la actividad por que llego
        drawerLayout.closeDrawer(GravityCompat.START)//se cierra el menu lateral cuando se selecciona una opcion
        return true
    }
}