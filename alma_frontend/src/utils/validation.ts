/**
 * Utilidades de validación de datos
 * Sincronizadas con las validaciones del backend
 */

/**
 * Validar DNI o NIE español (módulo 23)
 * @param dni DNI o NIE a validar
 * @returns true si es válido, false en caso contrario
 */
export const isValidDNI = (dni: string): boolean => {
  if (!dni) return false;

  // Eliminar espacios y convertir a mayúsculas
  dni = dni.replace(/\s/g, '').toUpperCase();

  // Validar formato: 8 dígitos + 1 letra
  const dniRegex = /^[0-9]{8}[A-Z]$/;
  const nieRegex = /^[XYZ][0-9]{7}[A-Z]$/;

  if (!dniRegex.test(dni) && !nieRegex.test(dni)) {
    return false;
  }

  // Convertir NIE a DNI para validación
  if (nieRegex.test(dni)) {
    dni = dni
      .replace(/^X/, '0')
      .replace(/^Y/, '1')
      .replace(/^Z/, '2');
  }

  // Letras de validación según módulo 23
  const letters = 'TRWAGMYFPDXBNJZSQVHLCKE';
  const number = parseInt(dni.substring(0, 8), 10);
  const letter = dni.charAt(8);

  // Validar que la letra corresponde con el número
  return letters.charAt(number % 23) === letter;
};

/**
 * Validar CIF español
 * @param cif CIF a validar
 * @returns true si es válido, false en caso contrario
 */
export const isValidCIF = (cif: string): boolean => {
  if (!cif) return false;

  // Eliminar espacios y convertir a mayúsculas
  cif = cif.replace(/\s/g, '').toUpperCase();

  // Validar formato: 1 letra + 7 dígitos + 1 dígito o letra
  const cifRegex = /^[ABCDEFGHJNPQRSUVW][0-9]{7}[0-9A-J]$/;

  if (!cifRegex.test(cif)) {
    return false;
  }

  // Tipos de organización (para determinar si debe terminar en número o letra)
  const mustBeNumber = 'ABEH';
  const mustBeLetter = 'KPQS';
  const firstChar = cif.charAt(0);
  const controlChar = cif.charAt(8);

  // Algoritmo de validación
  let sum = 0;
  const digits = cif.substring(1, 8);

  for (let i = 0; i < 7; i++) {
    const digit = parseInt(digits.charAt(i), 10);

    if (i % 2 === 0) {
      // Posiciones pares: multiplicar por 2 y sumar dígitos
      let double = digit * 2;
      sum += Math.floor(double / 10) + (double % 10);
    } else {
      // Posiciones impares: sumar directamente
      sum += digit;
    }
  }

  // Calcular dígito de control
  const unit = sum % 10;
  const controlDigit = unit === 0 ? 0 : 10 - unit;
  const controlLetter = 'JABCDEFGHI'.charAt(controlDigit);

  // Verificar según tipo
  if (mustBeNumber.includes(firstChar)) {
    return controlChar === controlDigit.toString();
  } else if (mustBeLetter.includes(firstChar)) {
    return controlChar === controlLetter;
  } else {
    // Puede ser número o letra
    return (
      controlChar === controlDigit.toString() || controlChar === controlLetter
    );
  }
};

/**
 * Validar formato de email
 * @param email Email a validar
 * @returns true si es válido, false en caso contrario
 */
export const isValidEmail = (email: string): boolean => {
  if (!email) return false;

  // Regex estándar para validación de email
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
  return emailRegex.test(email.trim());
};

/**
 * Validar contraseña (mínimo 8 caracteres según backend)
 * @param password Contraseña a validar
 * @returns true si es válida, false en caso contrario
 */
export const isValidPassword = (password: string): boolean => {
  if (!password) return false;
  return password.length >= 8;
};

/**
 * Validar número de teléfono español
 * @param telefono Número de teléfono a validar
 * @returns true si es válido, false en caso contrario
 */
export const isValidTelefono = (telefono: string): boolean => {
  if (!telefono) return true; // Opcional

  // Eliminar espacios, guiones y paréntesis
  const cleanPhone = telefono.replace(/[\s\-()]/g, '');

  // Validar formato español: 9 dígitos, puede empezar con +34
  const phoneRegex = /^(\+34)?[6789][0-9]{8}$/;
  return phoneRegex.test(cleanPhone);
};

/**
 * Validar número colegiado (formato básico)
 * @param numero Número colegiado a validar
 * @returns true si es válido, false en caso contrario
 */
export const isValidNumeroColegiado = (numero: string): boolean => {
  if (!numero) return true; // Opcional

  // Eliminar espacios
  const clean = numero.replace(/\s/g, '');

  // Al menos 4 caracteres alfanuméricos
  const colegiadoRegex = /^[A-Z0-9]{4,20}$/i;
  return colegiadoRegex.test(clean);
};

/**
 * Validar tarjeta sanitaria
 * @param tarjeta Tarjeta sanitaria a validar
 * @returns true si es válida, false en caso contrario
 */
export const isValidTarjetaSanitaria = (tarjeta: string): boolean => {
  if (!tarjeta) return true; // Opcional

  // Eliminar espacios
  const clean = tarjeta.replace(/\s/g, '');

  // Tarjeta sanitaria: entre 10 y 20 caracteres alfanuméricos
  const tarjetaRegex = /^[A-Z0-9]{10,20}$/i;
  return tarjetaRegex.test(clean);
};

/**
 * Validar fecha de nacimiento (debe ser una fecha pasada)
 * @param fecha Fecha en formato YYYY-MM-DD
 * @returns true si es válida, false en caso contrario
 */
export const isValidFechaNacimiento = (fecha: string): boolean => {
  if (!fecha) return true; // Opcional

  // Validar formato
  const fechaRegex = /^\d{4}-\d{2}-\d{2}$/;
  if (!fechaRegex.test(fecha)) {
    return false;
  }

  // Validar que sea una fecha válida
  const date = new Date(fecha);
  if (isNaN(date.getTime())) {
    return false;
  }

  // Validar que sea una fecha pasada
  const now = new Date();
  if (date >= now) {
    return false;
  }

  // Validar que no sea demasiado antigua (más de 120 años)
  const maxAge = new Date();
  maxAge.setFullYear(maxAge.getFullYear() - 120);
  if (date < maxAge) {
    return false;
  }

  return true;
};

/**
 * Formatear DNI/NIE con separadores
 * @param dni DNI o NIE
 * @returns DNI formateado
 */
export const formatDNI = (dni: string): string => {
  if (!dni) return '';

  const clean = dni.replace(/\s/g, '').toUpperCase();
  if (clean.length === 9) {
    return `${clean.substring(0, 8)}-${clean.charAt(8)}`;
  }
  return clean;
};

/**
 * Formatear CIF con separadores
 * @param cif CIF
 * @returns CIF formateado
 */
export const formatCIF = (cif: string): string => {
  if (!cif) return '';

  const clean = cif.replace(/\s/g, '').toUpperCase();
  if (clean.length === 9) {
    return `${clean.charAt(0)}-${clean.substring(1, 8)}-${clean.charAt(8)}`;
  }
  return clean;
};

/**
 * Formatear teléfono español
 * @param telefono Número de teléfono
 * @returns Teléfono formateado
 */
export const formatTelefono = (telefono: string): string => {
  if (!telefono) return '';

  const clean = telefono.replace(/[\s\-()]/g, '');

  if (clean.startsWith('+34')) {
    const number = clean.substring(3);
    return `+34 ${number.substring(0, 3)} ${number.substring(3, 6)} ${number.substring(6)}`;
  }

  if (clean.length === 9) {
    return `${clean.substring(0, 3)} ${clean.substring(3, 6)} ${clean.substring(6)}`;
  }

  return clean;
};

/**
 * Calcular edad a partir de fecha de nacimiento
 * @param fechaNacimiento Fecha en formato YYYY-MM-DD
 * @returns Edad en años
 */
export const calcularEdad = (fechaNacimiento: string): number => {
  if (!fechaNacimiento) return 0;

  const birthDate = new Date(fechaNacimiento);
  const today = new Date();

  let age = today.getFullYear() - birthDate.getFullYear();
  const monthDiff = today.getMonth() - birthDate.getMonth();

  if (monthDiff < 0 || (monthDiff === 0 && today.getDate() < birthDate.getDate())) {
    age--;
  }

  return age;
};

/**
 * Validar Número de Afiliación a la Seguridad Social (NASS)
 * Formato: PP/NNNNNNNN/DD donde:
 * - PP: Código de provincia (01-52)
 * - NNNNNNNN: 8 dígitos del número
 * - DD: 2 dígitos de control
 *
 * @param nass Número de afiliación a la Seguridad Social
 * @returns true si es válido, false en caso contrario
 */
export const isValidNumeroSeguridadSocial = (nass: string): boolean => {
  if (!nass) return false;

  // Eliminar espacios y barras para trabajar solo con dígitos
  const clean = nass.replace(/[\s/]/g, '');

  // Debe tener exactamente 12 dígitos
  if (!/^\d{12}$/.test(clean)) {
    return false;
  }

  // Extraer componentes
  const codigoProvincia = parseInt(clean.substring(0, 2), 10);
  const numeroBase = clean.substring(2, 10);
  const digitosControl = clean.substring(10, 12);

  // Validar código de provincia (01-52, más 66 para extranjeros)
  if ((codigoProvincia < 1 || codigoProvincia > 52) && codigoProvincia !== 66) {
    return false;
  }

  // Calcular dígitos de control
  // El algoritmo oficial: (PP + NNNNNNNN) % 97 = DD
  const numeroCompleto = parseInt(codigoProvincia.toString() + numeroBase, 10);
  const controlCalculado = numeroCompleto % 97;

  // Comparar con los dígitos de control proporcionados
  return controlCalculado === parseInt(digitosControl, 10);
};

/**
 * Formatear Número de Afiliación a la Seguridad Social
 * @param nass Número de afiliación
 * @returns NASS formateado como PP/NNNNNNNN/DD
 */
export const formatNumeroSeguridadSocial = (nass: string): string => {
  if (!nass) return '';

  const clean = nass.replace(/[\s/]/g, '');
  if (clean.length === 12) {
    return `${clean.substring(0, 2)}/${clean.substring(2, 10)}/${clean.substring(10, 12)}`;
  }
  return clean;
};

/**
 * Validar que un email NO sea de un dominio público/genérico
 * Para emails corporativos de organizaciones sanitarias
 * @param email Email a validar
 * @returns true si es corporativo, false si es dominio público
 */
export const isValidEmailCorporativo = (email: string): boolean => {
  if (!isValidEmail(email)) return false;

  // Lista de dominios públicos/gratuitos más comunes
  const dominiosPublicos = [
    'gmail.com',
    'hotmail.com',
    'outlook.com',
    'yahoo.com',
    'live.com',
    'icloud.com',
    'protonmail.com',
    'mail.com',
    'gmx.com',
    'aol.com',
    'yandex.com',
    'zoho.com',
    'tutanota.com',
    'temp-mail.org',
    'guerrillamail.com',
    '10minutemail.com',
    'mailinator.com',
  ];

  const dominio = email.split('@')[1]?.toLowerCase();

  if (!dominio) return false;

  // Verificar que no esté en la lista de dominios públicos
  return !dominiosPublicos.includes(dominio);
};

/**
 * Validar código REGCESS (Registro General de Centros, Servicios y Establecimientos Sanitarios)
 * Formato básico de validación, el formato real puede variar
 * @param codigo Código REGCESS
 * @returns true si tiene formato válido, false en caso contrario
 */
export const isValidCodigoREGCESS = (codigo: string): boolean => {
  if (!codigo) return false;

  // Eliminar espacios
  const clean = codigo.replace(/\s/g, '').toUpperCase();

  // Formato general: alfanumérico, entre 8 y 20 caracteres
  // Ejemplo real: puede ser como "1234567890" o "ES-1234567890"
  const regcessRegex = /^[A-Z0-9-]{8,20}$/;

  return regcessRegex.test(clean);
};
