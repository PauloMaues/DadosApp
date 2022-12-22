package br.com.riomaguari.dadosapp;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.text.TextUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Common {

    public static String codUsuario;
    public static void registraUsuario(String pcodUsuario) {
        codUsuario = pcodUsuario;
    }

    public static String macAddress;
    public static void registraMacAddress(String pmacAddress) {
        macAddress = pmacAddress;
    }

    public static String pmyNumo;
    public static void putNumo(String pNumo) {
        pmyNumo = pNumo;
    }

    public static String pmySit;
    public static void putSit(String pSit) {
        pmySit = pSit;
    }

    public static Boolean ePraAtualizar;

    public static boolean isOnline(Context c) {
        try {
            ConnectivityManager cm = (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (cm == null) throw new AssertionError();
            NetworkInfo netInfo = cm.getActiveNetworkInfo();
            return (netInfo != null && netInfo.isConnectedOrConnecting());

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /*
     * função para checar se está logado na rede local do estaleiro ou não.
     * */
    public static boolean isLocal(Context c) {

        WifiManager wifiManager = (WifiManager) c.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        String ssid = "", macAddress = "";

        if (wifiManager != null) {
            WifiInfo wifiInfo;
            wifiInfo = wifiManager.getConnectionInfo();
            if (wifiInfo.getSupplicantState() == SupplicantState.COMPLETED) {
                ssid = wifiInfo.getSSID();
            }
        }

/*        AlertDialog.Builder builder = new AlertDialog.Builder(c);
        builder.setMessage("Conectado no wifi -> "+ssid)
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //do things
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();*/

        return  ssid.contains("MagWireless");
    }

    public static String RemoveEspacosRepetidos(String s) {

        StringBuilder stringBuilder = new StringBuilder();
        String letraAtual, proximaLetra;

        for (int i = 0; i < s.length(); i++) {
            //se for espaço
            letraAtual = s.substring(i, i+1);
            if (letraAtual.equals(" ")) {
                //vou testar os próximos até encontrar um caracter que não seja espaço se não chegou no fim
                if (i+1 < s.length()) {
                    for (int j = i+1; j < s.length(); j++) {
                        //se a próxima não for espaço
                        proximaLetra = s.substring(j, j+1);
                        if (!proximaLetra.equals(" ")) {
                            stringBuilder.append(" ").append(proximaLetra);
                            i = j;
                            break;
                        }
                    }
                }
            //se não for espaço
            } else {
                stringBuilder.append(letraAtual);
            }
        }
        //remove múltiplos linefeeds por 1 somente
        return stringBuilder.toString().replaceAll("\\R+", "\n");
    }

    public static String capitalize(String capString){
        StringBuffer capBuffer = new StringBuffer();
        Matcher capMatcher = Pattern.compile("([a-z])([a-z]*)", Pattern.CASE_INSENSITIVE).matcher(capString);
        while (capMatcher.find()){
            capMatcher.appendReplacement(capBuffer, capMatcher.group(1).toUpperCase() + capMatcher.group(2).toLowerCase());
        }

        return capMatcher.appendTail(capBuffer).toString();
    }

    public static String adicionaLineFeed(String s) {

        String returnString = "";

        if (s == null || s.isEmpty()) return returnString;

        String[] splitS = s.split(",");

        for (int i = 0; i < splitS.length; i++) {
            returnString += String.format("%s<br>", splitS[i]);

        }

        //removo a última linha
        String newString = returnString.substring(0, returnString.lastIndexOf("<br>"));

        return newString;

    }

    public static final SimpleDateFormat dmyFormat = new SimpleDateFormat("dd/MM/yyyy");

    /*
     * função para converter data.
     * */
    public static String converteData(String data) {
        String ano = TextUtils.substring(data, 0, 4);
        String mes = TextUtils.substring(data, 5, 7);
        String dia = TextUtils.substring(data, 8, 10);
        return dia + "/" + mes + "/" + ano;
    }

    /*
     * função para converter data time.
     * */
    public static String converteDataHora(String data) {
        String ano = TextUtils.substring(data,0,4 );
        String mes = TextUtils.substring(data,5,7 );
        String dia = TextUtils.substring(data,8,10 );
        String hora = TextUtils.substring(data,11,19 );

        return dia+"/"+mes+"/"+ano+" "+hora;
    }

    public static final SimpleDateFormat dmykmsFormat = new SimpleDateFormat("dd/MM/yyyy kk:mm:ss");

    public static int get_week_number(String date_str) throws java.text.ParseException {
        Calendar cl = Calendar.getInstance();
        Date date = (Date) dmyFormat.parse(date_str);
        cl.setTime(date);
        int week = cl.get(Calendar.WEEK_OF_YEAR);
        return week;
    }

    public static String getWeekAgoDateFromToday() {
        long time_aweekago = System.currentTimeMillis() - 7L * 24 * 60 * 60 * 1000;
        Date d = new Date(time_aweekago);
        return dmyFormat.format(d);
    }

    public static String getNWeeksAgoDateFromToday(int numSemanas) {
        long time_2weeksago = System.currentTimeMillis() - numSemanas * (7L * 24 * 60 * 60 * 1000);
        Date d = new Date(time_2weeksago);
        return dmyFormat.format(d);
    }

    public static String startOfNextWeeks(int numSemanas) {
        Calendar cal = Calendar.getInstance();

        cal.setTime(startOfThisWeek());
        cal.add(Calendar.WEEK_OF_YEAR, numSemanas);

        return dmyFormat.format(cal.getTime());
    }

    public static Date startOfThisWeek() {
        Calendar cal = Calendar.getInstance();

        int firstDay = cal.getFirstDayOfWeek();
        cal.set(Calendar.DAY_OF_WEEK, firstDay);

        return startOfDay(cal.getTime());
    }

    public static Date startOfDay(Date origDate) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(origDate);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        return cal.getTime();
    }

}