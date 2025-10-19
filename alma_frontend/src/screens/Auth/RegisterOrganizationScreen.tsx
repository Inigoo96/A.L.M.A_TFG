import React, {useState} from 'react';
import {
  View,
  Text,
  TextInput,
  TouchableOpacity,
  StyleSheet,
  Alert,
  ActivityIndicator,
  KeyboardAvoidingView,
  Platform,
  ScrollView,
  Image,
  Animated,
  Pressable,
} from 'react-native';
import authService from '../../services/authService';
import {colors, fontSize, spacing, borderRadius} from '../../theme';

const RegisterOrganizationScreen = ({navigation}: any) => {
  const [nombreOrganizacion, setNombreOrganizacion] = useState('');
  const [nombreOrganizacionFocused, setNombreOrganizacionFocused] = useState(false);
  const [nombreOrganizacionError, setNombreOrganizacionError] = useState('');
  
  const [cif, setCif] = useState('');
  const [cifFocused, setCifFocused] = useState(false);
  const [cifError, setCifError] = useState('');
  
  const [nombre, setNombre] = useState('');
  const [nombreFocused, setNombreFocused] = useState(false);
  const [nombreError, setNombreError] = useState('');
  
  const [apellidos, setApellidos] = useState('');
  const [apellidosFocused, setApellidosFocused] = useState(false);
  const [apellidosError, setApellidosError] = useState('');
  
  const [email, setEmail] = useState('');
  const [emailFocused, setEmailFocused] = useState(false);
  const [emailError, setEmailError] = useState('');
  
  const [password, setPassword] = useState('');
  const [passwordFocused, setPasswordFocused] = useState(false);
  const [passwordError, setPasswordError] = useState('');
  const [showPassword, setShowPassword] = useState(false);
  const [passwordStrength, setPasswordStrength] = useState({
    score: 0,
    hasMinLength: false,
    hasUpperCase: false,
    hasLowerCase: false,
    hasNumber: false,
    hasSpecialChar: false,
  });
  
  const [confirmPassword, setConfirmPassword] = useState('');
  const [confirmPasswordFocused, setConfirmPasswordFocused] = useState(false);
  const [confirmPasswordError, setConfirmPasswordError] = useState('');
  const [showConfirmPassword, setShowConfirmPassword] = useState(false);
  
  const [loading, setLoading] = useState(false);
  
  // Animación para el botón
  const buttonScale = React.useRef(new Animated.Value(1)).current;

  const validateCIF = (cif: string): boolean => {
    const cifRegex = /^[A-Z][0-9]{8}$/;
    return cifRegex.test(cif.toUpperCase());
  };

  const validateEmail = (email: string): boolean => {
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return emailRegex.test(email);
  };

  const checkPasswordStrength = (password: string) => {
    const strength = {
      score: 0,
      hasMinLength: password.length >= 8,
      hasUpperCase: /[A-Z]/.test(password),
      hasLowerCase: /[a-z]/.test(password),
      hasNumber: /[0-9]/.test(password),
      hasSpecialChar: /[!@#$%^&*(),.?":{}|<>]/.test(password),
    };

    // Calcular score
    if (strength.hasMinLength) strength.score += 20;
    if (strength.hasUpperCase) strength.score += 20;
    if (strength.hasLowerCase) strength.score += 20;
    if (strength.hasNumber) strength.score += 20;
    if (strength.hasSpecialChar) strength.score += 20;

    return strength;
  };

  // Handle input changes with error clearing
  const handleNombreOrganizacionChange = (text: string) => {
    setNombreOrganizacion(text);
    if (nombreOrganizacionError && text.trim()) {
      setNombreOrganizacionError('');
    }
  };

  const handleCIFChange = (text: string) => {
    setCif(text);
    if (cifError && text.trim()) {
      setCifError('');
    }
  };

  const handleNombreChange = (text: string) => {
    setNombre(text);
    if (nombreError && text.trim()) {
      setNombreError('');
    }
  };

  const handleApellidosChange = (text: string) => {
    setApellidos(text);
    if (apellidosError && text.trim()) {
      setApellidosError('');
    }
  };

  const handleEmailChange = (text: string) => {
    setEmail(text);
    if (emailError && text.trim()) {
      setEmailError('');
    }
  };

  const handlePasswordChange = (text: string) => {
    setPassword(text);
    setPasswordStrength(checkPasswordStrength(text));
    if (passwordError && text.trim()) {
      setPasswordError('');
    }
  };

  const handleConfirmPasswordChange = (text: string) => {
    setConfirmPassword(text);
    if (confirmPasswordError && text.trim()) {
      setConfirmPasswordError('');
    }
  };

  // Button animation handlers
  const handlePressIn = () => {
    Animated.spring(buttonScale, {
      toValue: 0.96,
      useNativeDriver: true,
    }).start();
  };

  const handlePressOut = () => {
    Animated.spring(buttonScale, {
      toValue: 1,
      friction: 3,
      tension: 40,
      useNativeDriver: true,
    }).start();
  };

  const handleRegister = async () => {
    // Limpiar errores previos
    setNombreOrganizacionError('');
    setCifError('');
    setNombreError('');
    setApellidosError('');
    setEmailError('');
    setPasswordError('');
    setConfirmPasswordError('');

    let hasErrors = false;

    // Validar nombre organización
    if (!nombreOrganizacion.trim()) {
      setNombreOrganizacionError('El nombre de la organización es obligatorio');
      hasErrors = true;
    }

    // Validar CIF
    if (!cif.trim()) {
      setCifError('El CIF es obligatorio');
      hasErrors = true;
    } else if (!validateCIF(cif)) {
      setCifError('El CIF debe tener el formato correcto (ej: A12345678)');
      hasErrors = true;
    }

    // Validar nombre administrador
    if (!nombre.trim()) {
      setNombreError('El nombre es obligatorio');
      hasErrors = true;
    }

    // Validar apellidos
    if (!apellidos.trim()) {
      setApellidosError('Los apellidos son obligatorios');
      hasErrors = true;
    }

    // Validar email
    if (!email.trim()) {
      setEmailError('El correo electrónico es obligatorio');
      hasErrors = true;
    } else if (!validateEmail(email.trim())) {
      setEmailError('El formato del correo no es válido');
      hasErrors = true;
    }

    // Validar contraseña
    if (!password) {
      setPasswordError('La contraseña es obligatoria');
      hasErrors = true;
    } else if (password.length < 8) {
      setPasswordError('La contraseña debe tener al menos 8 caracteres');
      hasErrors = true;
    }

    // Validar confirmación de contraseña
    if (!confirmPassword) {
      setConfirmPasswordError('Debe confirmar la contraseña');
      hasErrors = true;
    } else if (password !== confirmPassword) {
      setConfirmPasswordError('Las contraseñas no coinciden');
      hasErrors = true;
    }

    if (hasErrors) {
      return;
    }

    setLoading(true);

    try {
      await authService.registerOrganization({
        nombreOrganizacion: nombreOrganizacion.trim(),
        cif: cif.toUpperCase().trim(),
        nombre: nombre.trim(),
        apellidos: apellidos.trim(),
        email: email.trim(),
        password,
      });

      Alert.alert(
        '¡Registro exitoso!',
        'Tu organización ha sido registrada correctamente',
        [
          {
            text: 'Continuar',
            onPress: () => navigation.replace('Dashboard'),
          },
        ]
      );
    } catch (error: any) {
      Alert.alert('Error de registro', error.message);
    } finally {
      setLoading(false);
    }
  };

  return (
    <KeyboardAvoidingView
      behavior={Platform.OS === 'ios' ? 'padding' : 'height'}
      style={styles.container}>
      <ScrollView
        contentContainerStyle={styles.scrollContent}
        keyboardShouldPersistTaps="handled">
        <View style={styles.header}>
          <Image
            source={require('../../assets/images/alma_logo.png')}
            style={styles.logo}
            resizeMode="contain"
          />
        </View>

        <View style={styles.formContainer}>
          <Text style={styles.title}>Registro de Organización</Text>
          <Text style={styles.subtitle}>
            Complete los datos para registrar su organización
          </Text>

          {/* Nombre Organización Input */}
          <View style={styles.inputSection}>
            <View style={styles.labelRow}>
              <Text style={styles.inputLabel}>Nombre de la organización</Text>
            </View>
            <View
              style={[
                styles.inputContainer,
                nombreOrganizacionFocused && styles.inputContainerFocused,
                nombreOrganizacionError && styles.inputContainerError,
              ]}>
              <Text style={styles.inputIcon}>🏢</Text>
              <TextInput
                style={styles.input}
                placeholder="Mi Organización"
                value={nombreOrganizacion}
                onChangeText={handleNombreOrganizacionChange}
                onFocus={() => setNombreOrganizacionFocused(true)}
                onBlur={() => setNombreOrganizacionFocused(false)}
                autoCapitalize="words"
                editable={!loading}
                placeholderTextColor={colors.mediumGreen}
              />
            </View>
            {nombreOrganizacionError ? (
              <Text style={styles.errorText}>{nombreOrganizacionError}</Text>
            ) : null}
          </View>

          {/* CIF Input */}
          <View style={styles.inputSection}>
            <View style={styles.labelRow}>
              <Text style={styles.inputLabel}>CIF</Text>
            </View>
            <View
              style={[
                styles.inputContainer,
                cifFocused && styles.inputContainerFocused,
                cifError && styles.inputContainerError,
              ]}>
              <Text style={styles.inputIcon}>📄</Text>
              <TextInput
                style={styles.input}
                placeholder="A12345678"
                value={cif}
                onChangeText={handleCIFChange}
                onFocus={() => setCifFocused(true)}
                onBlur={() => setCifFocused(false)}
                autoCapitalize="characters"
                maxLength={9}
                editable={!loading}
                placeholderTextColor={colors.mediumGreen}
              />
            </View>
            {cifError ? <Text style={styles.errorText}>{cifError}</Text> : null}
          </View>

          {/* Separador visual */}
          <View style={styles.divider}>
            <View style={styles.dividerLine} />
            <Text style={styles.dividerText}>Datos del Administrador</Text>
            <View style={styles.dividerLine} />
          </View>

          {/* Nombre Admin Input */}
          <View style={styles.inputSection}>
            <View style={styles.labelRow}>
              <Text style={styles.inputLabel}>Nombre del administrador</Text>
            </View>
            <View
              style={[
                styles.inputContainer,
                nombreFocused && styles.inputContainerFocused,
                nombreError && styles.inputContainerError,
              ]}>
              <Text style={styles.inputIcon}>👤</Text>
              <TextInput
                style={styles.input}
                placeholder="Nombre"
                value={nombre}
                onChangeText={handleNombreChange}
                onFocus={() => setNombreFocused(true)}
                onBlur={() => setNombreFocused(false)}
                autoCapitalize="words"
                editable={!loading}
                placeholderTextColor={colors.mediumGreen}
              />
            </View>
            {nombreError ? <Text style={styles.errorText}>{nombreError}</Text> : null}
          </View>

          {/* Apellidos Input */}
          <View style={styles.inputSection}>
            <View style={styles.labelRow}>
              <Text style={styles.inputLabel}>Apellidos</Text>
            </View>
            <View
              style={[
                styles.inputContainer,
                apellidosFocused && styles.inputContainerFocused,
                apellidosError && styles.inputContainerError,
              ]}>
              <Text style={styles.inputIcon}>👤</Text>
              <TextInput
                style={styles.input}
                placeholder="Apellidos"
                value={apellidos}
                onChangeText={handleApellidosChange}
                onFocus={() => setApellidosFocused(true)}
                onBlur={() => setApellidosFocused(false)}
                autoCapitalize="words"
                editable={!loading}
                placeholderTextColor={colors.mediumGreen}
              />
            </View>
            {apellidosError ? (
              <Text style={styles.errorText}>{apellidosError}</Text>
            ) : null}
          </View>

          {/* Email Input */}
          <View style={styles.inputSection}>
            <View style={styles.labelRow}>
              <Text style={styles.inputLabel}>Correo electrónico</Text>
            </View>
            <View
              style={[
                styles.inputContainer,
                emailFocused && styles.inputContainerFocused,
                emailError && styles.inputContainerError,
              ]}>
              <Text style={styles.inputIcon}>✉️</Text>
              <TextInput
                style={styles.input}
                placeholder="tu@email.com"
                value={email}
                onChangeText={handleEmailChange}
                onFocus={() => setEmailFocused(true)}
                onBlur={() => setEmailFocused(false)}
                keyboardType="email-address"
                autoCapitalize="none"
                autoCorrect={false}
                editable={!loading}
                placeholderTextColor={colors.mediumGreen}
              />
            </View>
            {emailError ? <Text style={styles.errorText}>{emailError}</Text> : null}
          </View>

          {/* Password Input */}
          <View style={styles.inputSection}>
            <View style={styles.labelRow}>
              <Text style={styles.inputLabel}>Contraseña</Text>
            </View>
            <View
              style={[
                styles.inputContainer,
                passwordFocused && styles.inputContainerFocused,
                passwordError && styles.inputContainerError,
              ]}>
              <Text style={styles.inputIcon}>🔒</Text>
              <TextInput
                style={styles.input}
                placeholder="••••••••"
                value={password}
                onChangeText={handlePasswordChange}
                onFocus={() => setPasswordFocused(true)}
                onBlur={() => setPasswordFocused(false)}
                secureTextEntry={!showPassword}
                autoCapitalize="none"
                editable={!loading}
                placeholderTextColor={colors.mediumGreen}
              />
              <TouchableOpacity
                onPress={() => setShowPassword(!showPassword)}
                style={styles.eyeButton}
                disabled={loading}>
                <Text style={styles.eyeText}>
                  {showPassword ? 'Ocultar' : 'Mostrar'}
                </Text>
              </TouchableOpacity>
            </View>
            {!passwordError && password ? (
              <View style={styles.passwordStrengthContainer}>
                <View style={styles.strengthMeter}>
                  <View
                    style={[
                      styles.strengthIndicator,
                      {
                        width: `${passwordStrength.score}%`,
                        backgroundColor:
                          passwordStrength.score < 40
                            ? '#e74c3c'
                            : passwordStrength.score < 80
                            ? '#f1c40f'
                            : '#2ecc71',
                      },
                    ]}
                  />
                </View>
                <Text style={styles.strengthText}>
                  {passwordStrength.score < 40
                    ? 'Débil'
                    : passwordStrength.score < 80
                    ? 'Media'
                    : 'Fuerte'}
                </Text>
                <View style={styles.requirementsList}>
                  <Text
                    style={[
                      styles.requirementText,
                      passwordStrength.hasMinLength && styles.requirementMet,
                    ]}>
                    ✓ Mínimo 8 caracteres
                  </Text>
                  <Text
                    style={[
                      styles.requirementText,
                      passwordStrength.hasUpperCase && styles.requirementMet,
                    ]}>
                    ✓ Al menos una mayúscula
                  </Text>
                  <Text
                    style={[
                      styles.requirementText,
                      passwordStrength.hasLowerCase && styles.requirementMet,
                    ]}>
                    ✓ Al menos una minúscula
                  </Text>
                  <Text
                    style={[
                      styles.requirementText,
                      passwordStrength.hasNumber && styles.requirementMet,
                    ]}>
                    ✓ Al menos un número
                  </Text>
                  <Text
                    style={[
                      styles.requirementText,
                      passwordStrength.hasSpecialChar && styles.requirementMet,
                    ]}>
                    ✓ Al menos un carácter especial
                  </Text>
                </View>
              </View>
            ) : passwordError ? (
              <Text style={styles.errorText}>{passwordError}</Text>
            ) : null}
          </View>

          {/* Confirm Password Input */}
          <View style={styles.inputSection}>
            <View style={styles.labelRow}>
              <Text style={styles.inputLabel}>Confirmar contraseña</Text>
            </View>
            <View
              style={[
                styles.inputContainer,
                confirmPasswordFocused && styles.inputContainerFocused,
                confirmPasswordError && styles.inputContainerError,
              ]}>
              <Text style={styles.inputIcon}>🔒</Text>
              <TextInput
                style={styles.input}
                placeholder="••••••••"
                value={confirmPassword}
                onChangeText={handleConfirmPasswordChange}
                onFocus={() => setConfirmPasswordFocused(true)}
                onBlur={() => setConfirmPasswordFocused(false)}
                secureTextEntry={!showConfirmPassword}
                autoCapitalize="none"
                editable={!loading}
                placeholderTextColor={colors.mediumGreen}
              />
              <TouchableOpacity
                onPress={() => setShowConfirmPassword(!showConfirmPassword)}
                style={styles.eyeButton}
                disabled={loading}>
                <Text style={styles.eyeText}>
                  {showConfirmPassword ? 'Ocultar' : 'Mostrar'}
                </Text>
              </TouchableOpacity>
            </View>
            {confirmPasswordError ? (
              <Text style={styles.errorText}>{confirmPasswordError}</Text>
            ) : confirmPassword ? (
              <View style={styles.passwordMatchContainer}>
                <View
                  style={[
                    styles.passwordMatchIndicator,
                    password === confirmPassword
                      ? styles.passwordMatchSuccess
                      : styles.passwordMatchError,
                  ]}>
                  <Text style={styles.passwordMatchIcon}>
                    {password === confirmPassword ? '✓' : '×'}
                  </Text>
                </View>
                <Text
                  style={[
                    styles.passwordMatchText,
                    password === confirmPassword
                      ? styles.passwordMatchTextSuccess
                      : styles.passwordMatchTextError,
                  ]}>
                  {password === confirmPassword
                    ? 'Las contraseñas coinciden'
                    : 'Las contraseñas no coinciden'}
                </Text>
              </View>
            ) : null}
          </View>

          {/* Register Button */}
          <Pressable
            onPressIn={handlePressIn}
            onPressOut={handlePressOut}
            onPress={handleRegister}
            disabled={loading}>
            <Animated.View
              style={[
                styles.button,
                loading && styles.buttonDisabled,
                {transform: [{scale: buttonScale}]},
              ]}>
              {loading ? (
                <ActivityIndicator color={colors.white} />
              ) : (
                <View style={styles.buttonContent}>
                  <Text style={styles.buttonText}>Registrar Organización</Text>
                  <Text style={styles.buttonIcon}>→</Text>
                </View>
              )}
            </Animated.View>
          </Pressable>

          {/* Back Button with Animation */}
          <View style={styles.footerSection}>
            <Pressable
              onPressIn={() => {
                Animated.spring(buttonScale, {
                  toValue: 0.96,
                  useNativeDriver: true,
                }).start();
              }}
              onPressOut={() => {
                Animated.spring(buttonScale, {
                  toValue: 1,
                  friction: 3,
                  tension: 40,
                  useNativeDriver: true,
                }).start();
              }}
              onPress={() => navigation.goBack()}
              disabled={loading}>
              <Animated.View
                style={[
                  styles.backButton,
                  loading && styles.buttonDisabled,
                  {transform: [{scale: buttonScale}]},
                ]}>
                <Text style={styles.backButtonIcon}>←</Text>
                <Text style={styles.backButtonText}>Volver al inicio</Text>
              </Animated.View>
            </Pressable>
          </View>
        </View>
      </ScrollView>
    </KeyboardAvoidingView>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: colors.background,
  },
  scrollContent: {
    flexGrow: 1,
    padding: spacing.lg,
  },
  header: {
    alignItems: 'center',
    marginBottom: spacing.lg,
  },
  logo: {
    width: 100,
    height: 100,
    marginBottom: spacing.sm,
  },
  formContainer: {
    flex: 1,
    width: '100%',
  },
  title: {
    fontSize: fontSize.xxxl,
    fontWeight: 'bold',
    color: colors.darkGreen,
    textAlign: 'center',
    marginBottom: spacing.sm,
  },
  subtitle: {
    fontSize: fontSize.md,
    color: colors.mediumGreen,
    textAlign: 'center',
    marginBottom: spacing.xl,
    fontStyle: 'italic',
  },
  // Sección de input con label
  inputSection: {
    marginBottom: spacing.md,
  },
  labelRow: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    marginBottom: spacing.sm,
    marginLeft: spacing.xs,
    marginRight: spacing.xs,
  },
  inputLabel: {
    fontSize: fontSize.sm,
    fontWeight: '600',
    color: colors.darkGreen,
  },
  // Container del input con icono
  inputContainer: {
    flexDirection: 'row',
    alignItems: 'center',
    backgroundColor: '#f8f9fa',
    borderWidth: 2,
    borderColor: colors.border,
    borderRadius: borderRadius.md,
    paddingHorizontal: spacing.md,
    shadowColor: '#000',
    shadowOffset: {width: 0, height: 1},
    shadowOpacity: 0.05,
    shadowRadius: 2,
    elevation: 1,
  },
  inputContainerFocused: {
    borderColor: colors.primary,
    backgroundColor: colors.white,
    shadowOpacity: 0.1,
    shadowRadius: 4,
    elevation: 3,
  },
  inputContainerError: {
    borderColor: '#e74c3c',
    backgroundColor: '#fff5f5',
  },
  inputIcon: {
    fontSize: 20,
    marginRight: spacing.sm,
  },
  input: {
    flex: 1,
    paddingVertical: spacing.md,
    fontSize: fontSize.md,
    color: colors.textPrimary,
  },
  eyeButton: {
    padding: spacing.xs,
    marginLeft: spacing.xs,
  },
  eyeText: {
    fontSize: fontSize.xs,
    color: colors.primary,
    fontWeight: '600',
  },
  errorText: {
    fontSize: fontSize.xs,
    color: '#e74c3c',
    marginTop: spacing.xs,
    marginLeft: spacing.xs,
    fontWeight: '500',
  },
  // Botón mejorado con contenido y animación
  button: {
    backgroundColor: colors.primary,
    paddingVertical: spacing.lg,
    paddingHorizontal: spacing.xl,
    borderRadius: borderRadius.md,
    alignItems: 'center',
    justifyContent: 'center',
    marginTop: spacing.xl,
    shadowColor: colors.primary,
    shadowOffset: {width: 0, height: 4},
    shadowOpacity: 0.3,
    shadowRadius: 8,
    elevation: 6,
    minHeight: 56,
  },
  buttonDisabled: {
    backgroundColor: colors.mediumGreen,
    opacity: 0.6,
    shadowOpacity: 0.1,
  },
  buttonContent: {
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'center',
    gap: spacing.sm,
  },
  buttonText: {
    color: colors.white,
    fontSize: fontSize.lg,
    fontWeight: 'bold',
  },
  buttonIcon: {
    color: colors.white,
    fontSize: fontSize.xl,
    fontWeight: 'bold',
  },
  // Footer section with back button
  footerSection: {
    marginTop: spacing.lg,
    alignItems: 'center',
  },
  backButton: {
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'center',
    backgroundColor: colors.background,
    paddingVertical: spacing.md,
    paddingHorizontal: spacing.xl,
    borderRadius: borderRadius.md,
    borderWidth: 2,
    borderColor: colors.primary,
    shadowColor: colors.primary,
    shadowOffset: {width: 0, height: 2},
    shadowOpacity: 0.1,
    shadowRadius: 4,
    elevation: 2,
  },
  backButtonText: {
    color: colors.primary,
    fontSize: fontSize.md,
    fontWeight: 'bold',
    marginLeft: spacing.xs,
  },
  backButtonIcon: {
    color: colors.primary,
    fontSize: fontSize.xl,
    fontWeight: 'bold',
  },
  // Separador visual
  divider: {
    flexDirection: 'row',
    alignItems: 'center',
    marginVertical: spacing.xl,
    paddingHorizontal: spacing.md,
  },
  dividerLine: {
    flex: 1,
    height: 1,
    backgroundColor: colors.border,
  },
  dividerText: {
    marginHorizontal: spacing.md,
    color: colors.darkGreen,
    fontSize: fontSize.md,
    fontWeight: '600',
  },
  // Estilos del medidor de seguridad de contraseña
  passwordStrengthContainer: {
    marginTop: spacing.xs,
    padding: spacing.sm,
  },
  strengthMeter: {
    height: 4,
    backgroundColor: colors.border,
    borderRadius: 2,
    marginVertical: spacing.xs,
    overflow: 'hidden',
  },
  strengthIndicator: {
    height: '100%',
    borderRadius: 2,
  },
  strengthText: {
    fontSize: fontSize.xs,
    fontWeight: '600',
    marginBottom: spacing.xs,
    color: colors.darkGreen,
  },
  requirementsList: {
    marginTop: spacing.xs,
  },
  requirementText: {
    fontSize: fontSize.xs,
    color: colors.mediumGreen,
    marginVertical: 2,
  },
  requirementMet: {
    color: '#2ecc71',
    fontWeight: '600',
  },
  // Estilos del indicador de coincidencia de contraseñas
  passwordMatchContainer: {
    flexDirection: 'row',
    alignItems: 'center',
    marginTop: spacing.xs,
    paddingHorizontal: spacing.xs,
  },
  passwordMatchIndicator: {
    width: 24,
    height: 24,
    borderRadius: 12,
    alignItems: 'center',
    justifyContent: 'center',
    marginRight: spacing.xs,
  },
  passwordMatchSuccess: {
    backgroundColor: '#2ecc71',
  },
  passwordMatchError: {
    backgroundColor: '#e74c3c',
  },
  passwordMatchIcon: {
    color: colors.white,
    fontSize: fontSize.md,
    fontWeight: 'bold',
  },
  passwordMatchText: {
    fontSize: fontSize.xs,
    fontWeight: '500',
  },
  passwordMatchTextSuccess: {
    color: '#2ecc71',
  },
  passwordMatchTextError: {
    color: '#e74c3c',
  },
});

export default RegisterOrganizationScreen;