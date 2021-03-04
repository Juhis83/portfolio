package tikape.harjoitustyo;

import java.sql.*;
import java.util.Random;
import java.util.Scanner;

public class TiKaPeHarjoitustyo {

    public static void main(String[] args) throws SQLException {
        while (true) {
            Scanner lukija = new Scanner(System.in);
            System.out.print("Valitse toiminto (1-9), 0 lopettaa: ");
            int toiminto = Integer.valueOf(lukija.nextLine());

            if (toiminto == 1) {
                luoTaulut();
                System.out.println("Tietokanta luotu");
            }

            if (toiminto == 2) {
                System.out.print("Anna paikan nimi: ");
                String paikanNimi = lukija.nextLine();
                lisaaPaikka(paikanNimi);
            }

            if (toiminto == 3) {
                System.out.print("Anna asiakkaan nimi: ");
                String asiakkaanNimi = lukija.nextLine();
                lisaaAsiakas(asiakkaanNimi);
            }

            if (toiminto == 4) {
                System.out.print("Anna paketin seurantakoodi: ");
                String seurantakoodi = lukija.nextLine();
                System.out.print("Anna asiakkaan nimi: ");
                String asiakas = lukija.nextLine();
                lisaaPaketti(seurantakoodi, asiakas);
            }

            if (toiminto == 5) {
                System.out.print("Anna paketin seurantakoodi: ");
                String seurantakoodi = lukija.nextLine();
                System.out.print("Anna tapahtuman paikka: ");
                String paikka = lukija.nextLine();
                System.out.print("Anna tapahtuman kuvaus: ");
                String kuvaus = lukija.nextLine();
                lisaaTapahtuma(seurantakoodi, paikka, kuvaus);
            }

            if (toiminto == 6) {
                System.out.print("Anna paketin seurantakoodi: ");
                String seurantakoodi = lukija.nextLine();
                tulostaTapahtumat(seurantakoodi);
            }

            if (toiminto == 7) {
                System.out.print("Anna asiakkaan nimi: ");
                String asiakas = lukija.nextLine();
                tulostaPaketit(asiakas);
            }

            if (toiminto == 8) {
                System.out.print("Anna paikan nimi: ");
                String paikka = lukija.nextLine();
                System.out.print("Anna päivämäärä (muodossa dd.mm.yyyy): ");
                String paivays = lukija.nextLine();
                String paiva = paivays.substring(0, 2);
                String kuukausi = paivays.substring(3, 5);
                String vuosi = paivays.substring(6, 10);

                tulostaTapahtumienMaara(paikka, paiva, kuukausi, vuosi);
            }

            if (toiminto == 9) {
                System.out.print("HUOM. Tietokantaa ei tule olla luotuna. Luodaanko ennen tehokkuustestin"
                        + " tekemistä uuteen tietokantaan indeksit (kyllä = K, ei = E)? ");
                String indeksitMukaan = lukija.nextLine();

                if (indeksitMukaan.equals("K")) {
                    luoIndeksoidutTaulut();
                    tehokkuustesti();
                } else if (indeksitMukaan.equals("E")) {
                    luoTaulut();
                    tehokkuustesti();
                }
            }

            if (toiminto == 0) {
                break;
            }
        }
    }

    public static void luoTaulut() throws SQLException {
        Connection db = DriverManager.getConnection("jdbc:sqlite:tietokanta.db");
        Statement s = db.createStatement();
        s.execute("CREATE TABLE Paikat (id INTEGER PRIMARY KEY, paikka TEXT UNIQUE)");
        s.execute("CREATE TABLE Asiakkaat (id INTEGER PRIMARY KEY, asiakas TEXT UNIQUE)");
        s.execute("CREATE TABLE Paketit (id INTEGER PRIMARY KEY, seurantakoodi TEXT UNIQUE, asiakas_id INTEGER REFERENCES Asiakkaat)");
        s.execute("CREATE TABLE Tapahtumat (id INTEGER PRIMARY KEY, paketin_seurantakoodi_id INTEGER REFERENCES Paketit, "
                + "paikka_id INTEGER REFERENCES Paikat, tapahtuman_kuvaus TEXT, lisaysaika TEXT)");

        s.execute("PRAGMA foreign_keys = ON;");

        db.close();
        s.close();
    }

    public static void luoIndeksoidutTaulut() throws SQLException {
        Connection db = DriverManager.getConnection("jdbc:sqlite:tietokanta.db");
        Statement s = db.createStatement();
        s.execute("CREATE TABLE Paikat (id INTEGER PRIMARY KEY, paikka TEXT UNIQUE)");
        s.execute("CREATE TABLE Asiakkaat (id INTEGER PRIMARY KEY, asiakas TEXT UNIQUE)");
        s.execute("CREATE TABLE Paketit (id INTEGER PRIMARY KEY, seurantakoodi TEXT UNIQUE, asiakas_id INTEGER REFERENCES Asiakkaat)");
        s.execute("CREATE TABLE Tapahtumat (id INTEGER PRIMARY KEY, paketin_seurantakoodi_id INTEGER REFERENCES Paketit, "
                + "paikka_id INTEGER REFERENCES Paikat, tapahtuman_kuvaus TEXT, lisaysaika TEXT)");

        s.execute("CREATE INDEX idx_asiakas_id ON Paketit (asiakas_id)");
        s.execute("CREATE INDEX idx_paketin_seurantakoodi_id ON Tapahtumat (paketin_seurantakoodi_id)");

        s.execute("PRAGMA foreign_keys = ON;");

        db.close();
        s.close();
    }

    public static void lisaaPaikka(String paikka) throws SQLException {
        Connection db = DriverManager.getConnection("jdbc:sqlite:tietokanta.db");

        PreparedStatement p = db.prepareStatement("INSERT INTO Paikat(paikka) VALUES (?)");
        p.setString(1, paikka);

        try {
            p.executeUpdate();
            System.out.println("paikka lisätty");
            db.close();
            p.close();
        } catch (SQLException e) {
            System.out.println("VIRHE: paikka on jo tietokannassa");
            db.close();
            p.close();
        }
        db.close();
        p.close();
    }

    public static void lisaaAsiakas(String asiakas) throws SQLException {
        Connection db = DriverManager.getConnection("jdbc:sqlite:tietokanta.db");

        PreparedStatement p = db.prepareStatement("INSERT INTO Asiakkaat(asiakas) VALUES (?)");
        p.setString(1, asiakas);

        try {
            p.executeUpdate();
            System.out.println("asiakas lisätty");
            db.close();
            p.close();
        } catch (SQLException e) {
            System.out.println("VIRHE: asiakas on jo tietokannassa");
            db.close();
            p.close();
        }

        db.close();
        p.close();
    }

    public static void lisaaPaketti(String seurantakoodi, String asiakas) throws SQLException {
        Connection db = DriverManager.getConnection("jdbc:sqlite:tietokanta.db");
        Statement s = db.createStatement();

        PreparedStatement p = db.prepareStatement("SELECT id FROM Asiakkaat WHERE asiakas=?");
        p.setString(1, asiakas);

        ResultSet r = p.executeQuery();

        try {
            s.execute("INSERT INTO Paketit (seurantakoodi, asiakas_id) VALUES ('" + seurantakoodi + "','" + r.getInt("id") + "')");
            System.out.println("paketti lisätty");
            db.close();
            s.close();
            p.close();
            r.close();
        } catch (SQLException e) {
            System.out.println("VIRHE: paketin seurantakoodi pitää olla uusi ja asiakas pitää löytyä tietokannasta");
            db.close();
            s.close();
            p.close();
            r.close();
        }

        db.close();
        s.close();
        p.close();
        r.close();
    }

    public static void lisaaTapahtuma(String seurantakoodi, String paikka, String kuvaus) throws SQLException {
        Connection db = DriverManager.getConnection("jdbc:sqlite:tietokanta.db");
        Statement s = db.createStatement();

        PreparedStatement p = db.prepareStatement("SELECT id FROM Paikat WHERE paikka=?");
        PreparedStatement k = db.prepareStatement("SELECT id FROM Paketit WHERE seurantakoodi=?");
        p.setString(1, paikka);
        k.setString(1, seurantakoodi);

        ResultSet r = p.executeQuery();
        ResultSet v = k.executeQuery();

        try {
            s.execute("INSERT INTO Tapahtumat (paketin_seurantakoodi_id, paikka_id, tapahtuman_kuvaus, lisaysaika) "
                    + "VALUES ('" + v.getInt("id") + "','" + r.getInt("id") + "','" + kuvaus + "','" + lisaaAikaleima() + "')");
            System.out.println("Uusi tapahtuma lisätty");
            db.close();
            s.close();
            p.close();
            k.close();
            r.close();
            v.close();
        } catch (SQLException e) {
            System.out.println("VIRHE: Lisääminen ei onnistunut. Paketin seurantakoodi ja paikka tulee olla syötettynä");
            db.close();
            s.close();
            p.close();
            k.close();
            r.close();
            v.close();
        }
        db.close();
        s.close();
        p.close();
        k.close();
        r.close();
        v.close();
    }

    public static java.sql.Timestamp lisaaAikaleima() {

        java.util.Date today = new java.util.Date();
        return new java.sql.Timestamp(today.getTime());

    }

    public static void tulostaTapahtumat(String seurantakoodi) throws SQLException {
        Connection db = DriverManager.getConnection("jdbc:sqlite:tietokanta.db");
        Statement s = db.createStatement();

        try {
            PreparedStatement p = db.prepareStatement("SELECT id FROM Paketit WHERE seurantakoodi=?");
            p.setString(1, seurantakoodi);
            ResultSet k = p.executeQuery();

            ResultSet r = s.executeQuery("SELECT * FROM Tapahtumat WHERE paketin_seurantakoodi_id =" + k.getInt("id"));

            while (r.next()) {
                System.out.println(muokkaaTulostettavaAika(r.getString("lisaysaika")) + ", " 
                        + haePaikka(r.getInt("paikka_id")) + ", " + r.getString("tapahtuman_kuvaus"));
            }
            db.close();
            s.close();
            p.close();
            k.close();
            r.close();
        } catch (SQLException e) {
            System.out.println("VIRHE: Tulostaminen ei onnistunut");
            db.close();
            s.close();
        }
        db.close();
        s.close();
    }

    public static String muokkaaTulostettavaAika(String lisaysaika) {
        String palautettavaAika = "";
        String pv = lisaysaika.substring(8, 10);
        String kk = lisaysaika.substring(5, 7);
        String vuosi = lisaysaika.substring(0, 4);
        String tunnit = lisaysaika.substring(11, 13);
        String minuutit = lisaysaika.substring(14, 16);

        palautettavaAika = pv + "." + kk + "." + vuosi + " " + tunnit + ":" + minuutit;

        return palautettavaAika;
    }

    public static String haePaikka(int paikka_id) throws SQLException {
        Connection db = DriverManager.getConnection("jdbc:sqlite:tietokanta.db");
        PreparedStatement p = db.prepareStatement("SELECT paikka FROM Paikat WHERE id=?");
        p.setInt(1, paikka_id);
        ResultSet k = p.executeQuery();

        return k.getString("paikka");

    }

    public static void tulostaPaketit(String asiakas) throws SQLException {
        Connection db = DriverManager.getConnection("jdbc:sqlite:tietokanta.db");
        Statement s = db.createStatement();

        try {
            PreparedStatement p = db.prepareStatement("SELECT id FROM Asiakkaat WHERE asiakas=?");
            p.setString(1, asiakas);
            ResultSet k = p.executeQuery();

            ResultSet r = s.executeQuery("SELECT * FROM Paketit WHERE asiakas_id =" + k.getInt("id"));
            while (r.next()) {
                System.out.println(r.getString("seurantakoodi") + ", " + haeTapahtumienMaara(r.getInt("id")) + " tapahtumaa");
            }
            db.close();
            s.close();
            p.close();
            k.close();
            r.close();
        } catch (SQLException e) {
            System.out.println("VIRHE: Tulostaminen ei onnistunut");
            db.close();
            s.close();
        }
        db.close();
        s.close();
    }

    public static int haeTapahtumienMaara(int seurantakoodi_id) throws SQLException {
        Connection db = DriverManager.getConnection("jdbc:sqlite:tietokanta.db");
        PreparedStatement p = db.prepareStatement("SELECT COUNT(*) tulos FROM Tapahtumat WHERE paketin_seurantakoodi_id=?");
        p.setInt(1, seurantakoodi_id);
        ResultSet k = p.executeQuery();

        int palautus = k.getInt("tulos");

        db.close();
        p.close();
        k.close();

        return palautus;
    }

    public static void tulostaTapahtumienMaara(String paikka, String paiva, String kk, String vuosi) throws SQLException {
        Connection db = DriverManager.getConnection("jdbc:sqlite:tietokanta.db");
        Statement s = db.createStatement();

        try {
            PreparedStatement p = db.prepareStatement("SELECT id FROM Paikat WHERE paikka=?");
            p.setString(1, paikka);
            ResultSet k = p.executeQuery();

            ResultSet r = s.executeQuery("SELECT COUNT(*) arvo FROM Tapahtumat WHERE paikka_id =" + k.getInt("id") + " AND strftime('%d',lisaysaika)= '"
                    + paiva + "' " + "AND strftime('%m',lisaysaika)= '" + kk + "' " + "AND strftime('%Y',lisaysaika)= '" + vuosi + "'");

            System.out.println("Tapahtumien määrä: " + r.getInt("arvo"));

            db.close();
            s.close();
            p.close();
            k.close();
            r.close();
        } catch (SQLException e) {
            System.out.println("VIRHE: Tulostaminen ei onnistunut");
            db.close();
            s.close();
        }

        db.close();
        s.close();
    }

    public static void tehokkuustesti() throws SQLException {

        Connection db = DriverManager.getConnection("jdbc:sqlite:tietokanta.db");

        // 1. vaihe - lisätään tuhat paikkaa
        long aika_a1 = System.nanoTime();

        Statement s = db.createStatement();
        s.execute("BEGIN TRANSACTION");
        PreparedStatement p = db.prepareStatement("INSERT INTO Paikat (paikka) VALUES (?)");
        for (int i = 1; i <= 1000; i++) {
            p.setString(1, "P" + i);
            p.executeUpdate();
        }
        s.execute("COMMIT");

        long aika_a2 = System.nanoTime();
        System.out.println("1. Aikaa kului tuhannen paikan lisäämiseen " + (aika_a2 - aika_a1) / 1e9 + " sekuntia");

        // 2. vaihe - lisätään tuhat asiakasta
        long aika_b1 = System.nanoTime();

        Statement s2 = db.createStatement();
        s2.execute("BEGIN TRANSACTION");
        PreparedStatement p2 = db.prepareStatement("INSERT INTO Asiakkaat (asiakas) VALUES (?)");
        for (int i = 1; i <= 1000; i++) {
            p2.setString(1, "A" + i);
            p2.executeUpdate();
        }
        s.execute("COMMIT");

        long aika_b2 = System.nanoTime();
        System.out.println("2. Aikaa kului tuhannen asiakkaan lisäämiseen " + (aika_b2 - aika_b1) / 1e9 + " sekuntia");

        // 3. vaihe - lisätään tuhat pakettia
        long aika_c1 = System.nanoTime();

        Statement s3 = db.createStatement();
        s3.execute("BEGIN TRANSACTION");
        PreparedStatement p3 = db.prepareStatement("INSERT INTO Paketit (asiakas_id) VALUES (?)");
        for (int i = 1; i <= 1000; i++) {
            Random randomGenerator = new Random();
            int randomInt = randomGenerator.nextInt(1000) + 1;

            p3.setInt(1, randomInt);
            p3.executeUpdate();
        }
        s.execute("COMMIT");

        long aika_c2 = System.nanoTime();
        System.out.println("3. Aikaa kului tuhannen paketin lisäämiseen " + (aika_c2 - aika_c1) / 1e9 + " sekuntia");

        // 4. vaihe - lisätään miljoona tapahtumaa
        long aika_d1 = System.nanoTime();

        Statement s4 = db.createStatement();
        s4.execute("BEGIN TRANSACTION");
        PreparedStatement p4 = db.prepareStatement("INSERT INTO Tapahtumat (paketin_seurantakoodi_id, paikka_id) VALUES (?,?)");
        for (int i = 1; i <= 1000000; i++) {
            Random randomGenerator = new Random();
            int randomIntPaketti = randomGenerator.nextInt(1000) + 1;
            int randomIntPaikka = randomGenerator.nextInt(1000) + 1;

            p4.setInt(1, randomIntPaketti);
            p4.setInt(2, randomIntPaikka);

            p4.executeUpdate();
        }
        s.execute("COMMIT");

        long aika_d2 = System.nanoTime();
        System.out.println("4. Aikaa kului miljoonan tapahtuman lisäämiseen " + (aika_d2 - aika_d1) / 1e9 + " sekuntia");

        // 5. vaihe - suoritetaan tuhat kyselyä, joista jokaisessa haetaan jonkin asiakkaan pakettien määrä
        long aika_e1 = System.nanoTime();

        Statement s5 = db.createStatement();

        PreparedStatement p5 = db.prepareStatement("SELECT COUNT(*) tulos FROM Paketit WHERE Asiakas_id=?");
        for (int i = 1; i <= 1000; i++) {
            Random randomGenerator = new Random();
            int randomIntAsiakas = randomGenerator.nextInt(1000) + 1;

            p5.setInt(1, randomIntAsiakas);
            ResultSet r = p5.executeQuery();
        }

        long aika_e2 = System.nanoTime();
        System.out.println("5. Aikaa kului asiakkaan pakettien määrän hakemiseen (1000 kyselyä) " + (aika_e2 - aika_e1) / 1e9 + " sekuntia");

        // 6. vaihe - suoritetaan tuhat kyselyä, joista jokaisessa haetaan jonkin paketin tapahtumien määrä
        long aika_f1 = System.nanoTime();

        Statement s6 = db.createStatement();

        PreparedStatement p6 = db.prepareStatement("SELECT COUNT(*) tulos FROM Tapahtumat WHERE paketin_seurantakoodi_id=?");
        for (int i = 1; i <= 1000; i++) {
            Random randomGenerator = new Random();
            int randomIntPaketti = randomGenerator.nextInt(1000) + 1;

            p6.setInt(1, randomIntPaketti);
            ResultSet r = p6.executeQuery();
        }

        long aika_f2 = System.nanoTime();
        System.out.println("6. Aikaa kului paketin tapahtumien määrän hakemiseen (1000 kyselyä) " + (aika_f2 - aika_f1) / 1e9 + " sekuntia");
    }
}
