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

const LoginScreen = ({navigation}: any) => {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [loading, setLoading] = useState(false);
  const [emailFocused, setEmailFocused] = useState(false);
  const [passwordFocused, setPasswordFocused] = useState(false);
  const [showPassword, setShowPassword] = useState(false);
  const [emailError, setEmailError] = useState('');
  const [passwordError, setPasswordError] = useState('');

  // Animaciones para el botón
  const buttonScale = React.useRef(new Animated.Value(1)).current;

  const validateEmail = (email: string): boolean => {
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return emailRegex.test(email);
  };

  const handleEmailChange = (text: string) => {
    setEmail(text);
    if (emailError && text.trim()) {
      setEmailError('');
    }
  };

  const handlePasswordChange = (text: string) => {
    setPassword(text);
    if (passwordError && text.trim()) {
      setPasswordError('');
    }
  };

  const handleLogin = async () => {
    // Limpiar errores previos
    setEmailError('');
    setPasswordError('');

    // Validación de campos
    let hasErrors = false;

    if (!email.trim()) {
      setEmailError('El correo electrónico es obligatorio');
      hasErrors = true;
    } else if (!validateEmail(email.trim())) {
      setEmailError('El formato del correo no es válido');
      hasErrors = true;
    }

    if (!password.trim()) {
      setPasswordError('La contraseña es obligatoria');
      hasErrors = true;
    } else if (password.length < 8) {
      setPasswordError('La contraseña debe tener al menos 8 caracteres');
      hasErrors = true;
    }

    if (hasErrors) {
      return;
    }

    setLoading(true);

    try {
      const response = await authService.login(email.trim(), password);

      // Si el password es temporal, redirigir a cambio de contraseña
      if (response.password_temporal) {
        navigation.replace('ChangePassword');
      } else {
        // Si no es temporal, ir al Dashboard
        navigation.replace('Dashboard');
      }
    } catch (error: any) {
      Alert.alert('Error de autenticación', error.message);
    } finally {
      setLoading(false);
    }
  };

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
          <Text style={styles.formTitle}>Iniciar Sesión</Text>

          {/* Campo de Email con icono */}
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
            {emailError ? (
              <Text style={styles.errorText}>{emailError}</Text>
            ) : null}
          </View>

          {/* Campo de Contraseña con icono y botón mostrar/ocultar */}
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
            {passwordError ? (
              <Text style={styles.errorText}>{passwordError}</Text>
            ) : null}
            {!passwordError && (
              <TouchableOpacity
                onPress={() => navigation.navigate('ForgotPassword')}
                disabled={loading}
                style={styles.forgotPasswordButton}>
                <Text style={styles.forgotPasswordText}>
                  ¿Olvidaste tu contraseña?
                </Text>
              </TouchableOpacity>
            )}
          </View>

          {/* Botón de Iniciar Sesión con animación */}
          <Pressable
            onPressIn={handlePressIn}
            onPressOut={handlePressOut}
            onPress={handleLogin}
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
                  <Text style={styles.buttonText}>Iniciar Sesión</Text>
                  <Text style={styles.buttonIcon}>→</Text>
                </View>
              )}
            </Animated.View>
          </Pressable>

          {/* Separador visual mejorado */}
          <View style={styles.footerSection}>
            <View style={styles.divider}>
              <View style={styles.dividerLine} />
              <Text style={styles.dividerText}>o</Text>
              <View style={styles.dividerLine} />
            </View>

            {/* Enlaces de ayuda */}
            <TouchableOpacity
              style={styles.linkButton}
              onPress={() => navigation.navigate('UserTypeSelection')}
              disabled={loading}>
              <Text style={styles.linkText}>
                ¿No tienes cuenta? Contacta con tu organización
              </Text>
            </TouchableOpacity>
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
    justifyContent: 'center',
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
  title: {
    fontSize: fontSize.xxxl,
    fontWeight: 'bold',
    color: colors.darkGreen,
  },
  subtitle: {
    fontSize: fontSize.md,
    color: colors.mediumGreen,
    fontStyle: 'italic',
  },
  formContainer: {
    width: '100%',
  },
  formTitle: {
    fontSize: fontSize.xxxl,
    fontWeight: 'bold',
    color: colors.darkGreen,
    marginBottom: spacing.xl,
    textAlign: 'center',
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
  forgotPasswordText: {
    fontSize: fontSize.xs,
    color: colors.primary,
    fontWeight: '600',
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
  forgotPasswordButton: {
    marginTop: spacing.sm,
    alignItems: 'flex-end',
    marginRight: spacing.xs,
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
  // Sección del footer con separación visual
  footerSection: {
    marginTop: spacing.lg,
  },
  divider: {
    flexDirection: 'row',
    alignItems: 'center',
    marginVertical: spacing.xl,
  },
  dividerLine: {
    flex: 1,
    height: 1,
    backgroundColor: colors.border,
  },
  dividerText: {
    marginHorizontal: spacing.md,
    color: colors.mediumGreen,
    fontSize: fontSize.sm,
    fontWeight: '500',
  },
  linkButton: {
    alignItems: 'center',
    paddingVertical: spacing.sm,
  },
  linkText: {
    color: colors.primary,
    fontSize: fontSize.md,
    fontWeight: '600',
    textAlign: 'center',
  },
});

export default LoginScreen;