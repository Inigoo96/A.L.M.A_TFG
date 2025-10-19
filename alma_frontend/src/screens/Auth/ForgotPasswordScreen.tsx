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

const ForgotPasswordScreen = ({navigation}: any) => {
  const [email, setEmail] = useState('');
  const [emailFocused, setEmailFocused] = useState(false);
  const [emailError, setEmailError] = useState('');
  const [isLoading, setIsLoading] = useState(false);

  // Animación para el botón
  const buttonScale = React.useRef(new Animated.Value(1)).current;

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

  const validateEmail = (email: string) => {
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return emailRegex.test(email);
  };

  const isFormValid = () => {
    return email.length > 0 && validateEmail(email);
  };

  const handleResetPassword = async () => {
    if (!isFormValid()) {
      Alert.alert('Error', 'Por favor, introduce un email válido');
      return;
    }

    setIsLoading(true);

    try {
      // TODO: Implementar la llamada al servicio de reseteo de contraseña
      // await authService.resetPassword(email);
      
      Alert.alert(
        'Éxito',
        'Se ha enviado un enlace de recuperación a tu correo electrónico',
        [
          {
            text: 'OK',
            onPress: () => navigation.navigate('Login'),
          },
        ],
      );
    } catch (error: any) {
      Alert.alert('Error', error.message);
    } finally {
      setIsLoading(false);
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
          <Text style={styles.title}>Recuperar contraseña</Text>

          <View style={styles.alertBox}>
            <Text style={styles.alertText}>
              Recibirás un correo con un enlace para establecer una nueva
              contraseña
            </Text>
          </View>

          {/* Campo de Email */}
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
                onChangeText={setEmail}
                onFocus={() => setEmailFocused(true)}
                onBlur={() => {
                  setEmailFocused(false);
                  if (email && !validateEmail(email)) {
                    setEmailError('Email no válido');
                  } else {
                    setEmailError('');
                  }
                }}
                keyboardType="email-address"
                autoCapitalize="none"
                editable={!isLoading}
                placeholderTextColor={colors.mediumGreen}
              />
            </View>
            {emailError ? (
              <Text style={styles.errorText}>{emailError}</Text>
            ) : null}
          </View>

          {/* Botón de enviar con animación */}
          <Pressable
            onPressIn={handlePressIn}
            onPressOut={handlePressOut}
            onPress={handleResetPassword}
            disabled={isLoading || !isFormValid()}>
            <Animated.View
              style={[
                styles.button,
                {transform: [{scale: buttonScale}]},
                !isFormValid() && styles.buttonDisabled,
              ]}>
              {isLoading ? (
                <ActivityIndicator color={colors.white} />
              ) : (
                <View style={styles.buttonContent}>
                  <Text style={styles.buttonText}>Enviar instrucciones</Text>
                  <Text style={styles.buttonIcon}>→</Text>
                </View>
              )}
            </Animated.View>
          </Pressable>

          {/* Botón de volver */}
          <Pressable
            onPress={() => navigation.goBack()}
            style={styles.backButton}>
            <Text style={styles.backButtonText}>Volver al inicio de sesión</Text>
          </Pressable>
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
    width: '100%',
  },
  title: {
    fontSize: fontSize.xxxl,
    fontWeight: 'bold',
    color: colors.darkGreen,
    marginBottom: spacing.xl,
    textAlign: 'center',
  },
  subtitle: {
    fontSize: fontSize.sm,
    color: colors.mediumGreen,
    textAlign: 'center',
    lineHeight: 20,
    marginBottom: spacing.lg,
  },
  alertBox: {
    backgroundColor: colors.info + '40',
    borderLeftWidth: 4,
    borderLeftColor: colors.info,
    padding: spacing.md,
    borderRadius: borderRadius.sm,
    marginBottom: spacing.lg,
  },
  alertText: {
    fontSize: fontSize.sm,
    color: colors.textSecondary,
  },
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
  backButton: {
    marginTop: spacing.lg,
    padding: spacing.md,
    alignItems: 'center',
  },
  backButtonText: {
    color: colors.primary,
    fontSize: fontSize.md,
    fontWeight: '600',
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
  errorText: {
    fontSize: fontSize.xs,
    color: '#e74c3c',
    marginTop: spacing.xs,
    marginLeft: spacing.xs,
    fontWeight: '500',
  },
});

export default ForgotPasswordScreen;