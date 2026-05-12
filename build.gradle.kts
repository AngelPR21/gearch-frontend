// Top-level build file where you can add configuration options common to all sub-projects/modules.

//Añadimos el id
plugins {
    alias(libs.plugins.android.application) apply false
    // Plugin de Google Services necesario para que Firebase funcione
    // apply false significa que no se aplica aqui sino en el modulo app
    id("com.google.gms.google-services") version "4.4.0" apply false
}