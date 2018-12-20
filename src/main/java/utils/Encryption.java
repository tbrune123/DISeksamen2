package utils;

public final class Encryption {

  public static String encryptDecryptXOR(String rawString) {

    // If encryption is enabled in Config.
    if (Config.getEncryption()) {



      // The key is predefined and hidden in code
      // TODO: Create a more complex code and store it somewhere better FIX


      // Stringbuilder enables you to play around with strings and make useful stuff
      StringBuilder thisIsEncrypted = new StringBuilder();

      // TODO: This is where the magic of XOR is happening. Are you able to explain what is going on?
      // Krypteringen foregår ved brug af XOR, som tager den første karakter i den
      // streng man ønsker at kryptere, omdanner den til binærtal, kombinere den med den første karakter
      // i XOR-nøglen hvorefter den omdanner til binærysl igen, for til sidst at omdanne hele kombinationen til en karakter igen.
      // Processen forsætter for hele strengen ønsket krypteret. Hvis strengen er længere end XOR-nøglen,
      // vil den starte forfra på XOR-nøglens karakterer. Muligt eksempel: Det første bogstav der
      // skal krypteres, er ''a'' og det første bogstav i krypteringsnøglen er ’'b’'. Den binære værdi af disse
      // er henholdsvis 0110 0001 og 0110 0010. Ved XOR krypteringen bliver 0000 0011, som svarer
      // til tallet ’'3'’.
      for (int i = 0; i < rawString.length(); i++) {
        thisIsEncrypted.append((char) (rawString.charAt(i) ^ Config.getEncKey()[i % Config.getEncKey().length]));
      }

      // We return the encrypted string
      return thisIsEncrypted.toString();

    } else {
      // We return without having done anything
      return rawString;
    }
  }
}
