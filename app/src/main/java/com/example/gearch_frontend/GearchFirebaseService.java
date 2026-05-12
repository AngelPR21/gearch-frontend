package com.example.gearch_frontend;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

// Servicio que recibe las notificaciones push de Firebase
// Se ejecuta en segundo plano aunque la app este cerrada
public class GearchFirebaseService extends FirebaseMessagingService {

    private static final String CHANNEL_ID = "gearch_notificaciones";

    // Se ejecuta cuando llega una notificacion
    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        // Obtenemos el titulo y el cuerpo de la notificacion
        String titulo = remoteMessage.getNotification() != null
                ? remoteMessage.getNotification().getTitle() : "Gearch";
        String cuerpo = remoteMessage.getNotification() != null
                ? remoteMessage.getNotification().getBody() : "";

        mostrarNotificacion(titulo, cuerpo);
    }

    // Se ejecuta cuando Firebase genera un nuevo token para este dispositivo
    // Hay que enviarlo al backend para que pueda mandar notificaciones a este dispositivo
    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        // El token se envia al backend desde LoginActivity cuando el usuario inicia sesion
    }

    private void mostrarNotificacion(String titulo, String cuerpo) {
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        // En Android 8 o superior hay que crear un canal de notificaciones
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel canal = new NotificationChannel(
                    CHANNEL_ID,
                    "Notificaciones Gearch",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            manager.createNotificationChannel(canal);
        }

        // Al pulsar la notificacion abre LoginActivity
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        // Construimos la notificacion
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(titulo)
                .setContentText(cuerpo)
                .setAutoCancel(true) // la notificacion desaparece al pulsar
                .setContentIntent(pendingIntent);

        manager.notify((int) System.currentTimeMillis(), builder.build());
    }
}