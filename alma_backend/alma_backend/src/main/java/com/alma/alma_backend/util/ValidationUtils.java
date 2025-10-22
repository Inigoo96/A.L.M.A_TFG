package com.alma.alma_backend.util;

import java.util.regex.Pattern;

/**
 * Utilidades para validación de documentos españoles (DNI, NIE, CIF).
 * Implementa los algoritmos oficiales de validación con dígito de control.
 */
public class ValidationUtils {

    // Patrones regex
    private static final Pattern DNI_PATTERN = Pattern.compile("^[0-9]{8}[A-Z]$");
    private static final Pattern NIE_PATTERN = Pattern.compile("^[XYZ][0-9]{7}[A-Z]$");
    private static final Pattern CIF_PATTERN = Pattern.compile("^[ABCDEFGHJNPQRSUVW][0-9]{7}[0-9A-J]$");

    // Tabla de letras para validación de DNI (módulo 23)
    private static final String DNI_LETTERS = "TRWAGMYFPDXBNJZSQVHLCKE";

    // Tabla de letras para validación de CIF
    private static final String CIF_LETTERS = "JABCDEFGHI";

    /**
     * Valida un DNI español usando el algoritmo módulo 23.
     * @param dni DNI en formato 12345678A
     * @return true si el DNI es válido, false en caso contrario
     */
    public static boolean isValidDNI(String dni) {
        if (dni == null || !DNI_PATTERN.matcher(dni.toUpperCase()).matches()) {
            return false;
        }

        try {
            int numeros = Integer.parseInt(dni.substring(0, 8));
            char letra = dni.toUpperCase().charAt(8);
            char letraEsperada = DNI_LETTERS.charAt(numeros % 23);

            return letra == letraEsperada;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Valida un NIE español (Número de Identidad de Extranjero).
     * @param nie NIE en formato X1234567A, Y1234567A o Z1234567A
     * @return true si el NIE es válido, false en caso contrario
     */
    public static boolean isValidNIE(String nie) {
        if (nie == null || !NIE_PATTERN.matcher(nie.toUpperCase()).matches()) {
            return false;
        }

        try {
            nie = nie.toUpperCase();
            // Reemplazar X, Y, Z por 0, 1, 2 respectivamente
            char primeraLetra = nie.charAt(0);
            String nieNumerico = switch (primeraLetra) {
                case 'X' -> "0" + nie.substring(1, 8);
                case 'Y' -> "1" + nie.substring(1, 8);
                case 'Z' -> "2" + nie.substring(1, 8);
                default -> nie.substring(1, 8);
            };

            int numeros = Integer.parseInt(nieNumerico);
            char letra = nie.charAt(8);
            char letraEsperada = DNI_LETTERS.charAt(numeros % 23);

            return letra == letraEsperada;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Valida un DNI o NIE español.
     * @param documento DNI o NIE
     * @return true si el documento es válido, false en caso contrario
     */
    public static boolean isValidDNIorNIE(String documento) {
        if (documento == null) {
            return false;
        }
        return isValidDNI(documento) || isValidNIE(documento);
    }

    /**
     * Valida un CIF español (Código de Identificación Fiscal) usando el algoritmo oficial.
     * @param cif CIF en formato A12345678
     * @return true si el CIF es válido, false en caso contrario
     */
    public static boolean isValidCIF(String cif) {
        if (cif == null || !CIF_PATTERN.matcher(cif.toUpperCase()).matches()) {
            return false;
        }

        try {
            cif = cif.toUpperCase();
            char tipoOrganizacion = cif.charAt(0);
            String digitosCentrales = cif.substring(1, 8);
            char digitoControl = cif.charAt(8);

            // Calcular suma de dígitos en posiciones pares
            int sumaPares = 0;
            for (int i = 1; i < 7; i += 2) {
                sumaPares += Character.getNumericValue(digitosCentrales.charAt(i));
            }

            // Calcular suma de dígitos en posiciones impares (multiplicados por 2)
            int sumaImpares = 0;
            for (int i = 0; i < 7; i += 2) {
                int digito = Character.getNumericValue(digitosCentrales.charAt(i));
                int multiplicado = digito * 2;
                sumaImpares += (multiplicado / 10) + (multiplicado % 10);
            }

            int sumaTotal = sumaPares + sumaImpares;
            int unidad = sumaTotal % 10;
            int digitoControlCalculado = (unidad == 0) ? 0 : (10 - unidad);

            // Dependiendo del tipo de organización, el dígito de control puede ser número o letra
            if ("NPQRSW".indexOf(tipoOrganizacion) >= 0) {
                // Solo se permiten letras
                return digitoControl == CIF_LETTERS.charAt(digitoControlCalculado);
            } else if ("ABEH".indexOf(tipoOrganizacion) >= 0) {
                // Solo se permiten números
                return Character.getNumericValue(digitoControl) == digitoControlCalculado;
            } else {
                // Se permiten ambos (número o letra)
                return Character.getNumericValue(digitoControl) == digitoControlCalculado
                    || digitoControl == CIF_LETTERS.charAt(digitoControlCalculado);
            }
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Valida un número de tarjeta sanitaria española (SIP/TSI).
     * El formato puede variar según la comunidad autónoma, pero generalmente son 20 dígitos.
     * Esta es una validación básica de formato.
     * @param tarjetaSanitaria Número de tarjeta sanitaria
     * @return true si el formato es válido, false en caso contrario
     */
    public static boolean isValidTarjetaSanitaria(String tarjetaSanitaria) {
        if (tarjetaSanitaria == null) {
            return false;
        }

        // Eliminar espacios y guiones
        String tarjetaLimpia = tarjetaSanitaria.replaceAll("[\\s-]", "");

        // Formato general: entre 10 y 20 caracteres alfanuméricos
        return tarjetaLimpia.matches("^[A-Z0-9]{10,20}$");
    }

    /**
     * Valida el formato de un número de colegiado.
     * El formato puede variar según el colegio profesional.
     * Esta es una validación básica de formato.
     * @param numeroColegiado Número de colegiado
     * @return true si el formato es válido, false en caso contrario
     */
    public static boolean isValidNumeroColegiado(String numeroColegiado) {
        if (numeroColegiado == null) {
            return false;
        }

        // Formato general: entre 4 y 20 caracteres alfanuméricos
        // Puede incluir barras y guiones
        String numeroLimpio = numeroColegiado.replaceAll("[\\s/-]", "");
        return numeroLimpio.matches("^[A-Z0-9]{4,20}$");
    }

    /**
     * Valida el formato de un código REGCESS (Registro General de Centros del SNS).
     * Formato: alfanumérico de 8 a 50 caracteres, puede incluir guiones.
     * Ejemplo: "ES-1234567890", "2800001234", "REGCESS-28-001"
     * @param codigoRegcess Código REGCESS
     * @return true si el formato es válido, false en caso contrario
     */
    public static boolean isValidCodigoREGCESS(String codigoRegcess) {
        if (codigoRegcess == null) {
            return false;
        }

        // Eliminar espacios
        String codigoLimpio = codigoRegcess.replaceAll("\\s", "").toUpperCase();

        // Formato general: alfanumérico, entre 8 y 50 caracteres, puede incluir guiones
        return codigoLimpio.matches("^[A-Z0-9-]{8,50}$");
    }

    /**
     * Valida el formato de un email corporativo.
     * Debe ser un email válido con dominio específico de la organización.
     * @param email Email a validar
     * @return true si el formato es válido, false en caso contrario
     */
    public static boolean isValidEmail(String email) {
        if (email == null) {
            return false;
        }

        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        return email.matches(emailRegex);
    }

    /**
     * Valida el formato de un teléfono español.
     * Acepta móviles (6XX, 7XX) y fijos (8XX, 9XX).
     * @param telefono Teléfono a validar
     * @return true si el formato es válido, false en caso contrario
     */
    public static boolean isValidTelefono(String telefono) {
        if (telefono == null) {
            return false;
        }

        // Eliminar espacios, guiones y paréntesis
        String telefonoLimpio = telefono.replaceAll("[\\s()-]", "");

        // Formato español: 9 dígitos, comenzando por 6, 7, 8 o 9
        // También acepta formato internacional: +34 seguido de 9 dígitos
        return telefonoLimpio.matches("^[6789][0-9]{8}$")
            || telefonoLimpio.matches("^\\+34[6789][0-9]{8}$")
            || telefonoLimpio.matches("^0034[6789][0-9]{8}$");
    }

    /**
     * Valida que un email sea corporativo (tenga dominio de la organización).
     * @param email Email a validar
     * @param nombreOrganizacion Nombre de la organización
     * @return true si el email es corporativo, false en caso contrario
     */
    public static boolean isEmailCorporativo(String email, String nombreOrganizacion) {
        if (email == null || nombreOrganizacion == null) {
            return false;
        }

        if (!isValidEmail(email)) {
            return false;
        }

        // Extraer el dominio del email
        String dominio = email.substring(email.indexOf("@") + 1).toLowerCase();
        String nombreOrg = nombreOrganizacion.toLowerCase()
            .replaceAll("[\\s.-]", "");

        // Verificar que el dominio contenga el nombre de la organización
        return dominio.contains(nombreOrg) || dominio.equals(nombreOrg + ".com")
            || dominio.equals(nombreOrg + ".es");
    }

    /**
     * Valida el Número de Afiliación a la Seguridad Social (NASS).
     * Formato: PP/NNNNNNNN/DD donde:
     * - PP: Código de provincia (01-52, o 66 para extranjeros)
     * - NNNNNNNN: 8 dígitos del número
     * - DD: 2 dígitos de control calculados mediante el algoritmo oficial (PP + NNNNNNNN) % 97
     *
     * @param nass Número de afiliación a la Seguridad Social
     * @return true si es válido, false en caso contrario
     */
    public static boolean isValidNumeroSeguridadSocial(String nass) {
        if (nass == null) {
            return false;
        }

        // Eliminar espacios y barras
        String nassLimpio = nass.replaceAll("[\\s/]", "");

        // Debe tener exactamente 12 dígitos
        if (!nassLimpio.matches("^\\d{12}$")) {
            return false;
        }

        try {
            // Extraer componentes
            int codigoProvincia = Integer.parseInt(nassLimpio.substring(0, 2));
            String numeroBase = nassLimpio.substring(2, 10);
            int digitosControl = Integer.parseInt(nassLimpio.substring(10, 12));

            // Validar código de provincia (01-52, más 66 para extranjeros)
            if ((codigoProvincia < 1 || codigoProvincia > 52) && codigoProvincia != 66) {
                return false;
            }

            // Calcular dígitos de control según algoritmo oficial
            // (PP + NNNNNNNN) % 97 = DD
            long numeroCompleto = Long.parseLong(codigoProvincia + numeroBase);
            int controlCalculado = (int) (numeroCompleto % 97);

            // Verificar que coincidan
            return controlCalculado == digitosControl;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Valida que un email NO sea de un dominio público o gratuito.
     * Útil para verificar emails corporativos en registros de organizaciones.
     *
     * @param email Email a validar
     * @return true si NO es dominio público (es corporativo), false si es dominio público
     */
    public static boolean isValidEmailCorporativo(String email) {
        if (!isValidEmail(email)) {
            return false;
        }

        // Lista de dominios públicos/gratuitos más comunes
        String[] dominiosPublicos = {
            "gmail.com", "hotmail.com", "outlook.com", "yahoo.com",
            "live.com", "icloud.com", "protonmail.com", "mail.com",
            "gmx.com", "aol.com", "yandex.com", "zoho.com",
            "tutanota.com", "temp-mail.org", "guerrillamail.com",
            "10minutemail.com", "mailinator.com"
        };

        // Extraer dominio del email
        String dominio = email.substring(email.indexOf("@") + 1).toLowerCase();

        // Verificar que NO esté en la lista de dominios públicos
        for (String dominioPublico : dominiosPublicos) {
            if (dominio.equals(dominioPublico)) {
                return false;
            }
        }

        return true;
    }
}