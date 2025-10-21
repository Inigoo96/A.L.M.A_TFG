import React, {useState} from 'react';
import {
  View,
  Text,
  TextInput,
  TouchableOpacity,
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
import AsyncStorage from '@react-native-async-storage/async-storage';
import {colors} from '../../theme';
import {styles} from '../../styles/screens/Auth/ChangePasswordScreen.styles';

const ChangePasswordScreen = ({navigation}: any) => {
  const [oldPassword, setOldPassword] = useState('');
  const [oldPasswordFocused, setOldPasswordFocused] = useState(false);
  const [oldPasswordError, setOldPasswordError] = useState('');
  const [showOldPassword, setShowOldPassword] = useState(false);

  const [newPassword, setNewPassword] = useState('');
  const [newPasswordFocused, setNewPasswordFocused] = useState(false);
  const [newPasswordError, setNewPasswordError] = useState('');
  const [showNewPassword, setShowNewPassword] = useState(false);

  const [confirmPassword, setConfirmPassword] = useState('');
  const [confirmPasswordFocused, setConfirmPasswordFocused] = useState(false);
  const [confirmPasswordError, setConfirmPasswordError] = useState('');
  const [showConfirmPassword, setShowConfirmPassword] = useState(false);

  const [loading, setLoading] = useState(false);

  // Animación para el botón
  const buttonScale = React.useRef(new Animated.Value(1)).current;
  const [passwordStrength, setPasswordStrength] = useState({
    score: 0,
    hasMinLength: false,
    hasUpperCase: false,
    hasLowerCase: false,
    hasNumber: false,
    hasSpecialChar: false,
  });

  const checkPasswordStrength = (password: string) => {
    const minLength = password.length >= 8;
    const hasUpper = /[A-Z]/.test(password);
    const hasLower = /[a-z]/.test(password);
    const hasNumber = /[0-9]/.test(password);
    const hasSpecial = /[!@#$%^&*(),.?":{}|<>]/.test(password);

    let score = 0;
    if (minLength) score++;
    if (hasUpper) score++;
    if (hasLower) score++;
    if (hasNumber) score++;
    if (hasSpecial) score++;

    setPasswordStrength({
      score,
      hasMinLength: minLength,
      hasUpperCase: hasUpper,
      hasLowerCase: hasLower,
      hasNumber: hasNumber,
      hasSpecialChar: hasSpecial,
    });
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

  const handleChangePassword = async () => {
    if (!oldPassword.trim() || !newPassword.trim() || !confirmPassword.trim()) {
      Alert.alert('Error', 'Por favor, completa todos los campos');
      return;
    }

    if (newPassword !== confirmPassword) {
      Alert.alert('Error', 'Las contraseñas nuevas no coinciden');
      return;
    }

    if (passwordStrength.score < 3) {
      Alert.alert(
        'Error',
        'La contraseña debe cumplir al menos tres de los siguientes requisitos:\n' +
        '- Mínimo 8 caracteres\n' +
        '- Al menos una mayúscula\n' +
        '- Al menos una minúscula\n' +
        '- Al menos un número\n' +
        '- Al menos un carácter especial',
      );
      return;
    }

    setLoading(true);

    try {
      await authService.updatePassword(oldPassword, newPassword);
      
      // Actualizar el estado de la contraseña temporal
      await AsyncStorage.setItem('password_temporal', 'false');

      Alert.alert(
        'Éxito',
        'Contraseña actualizada correctamente. Ya puedes usar todas las funciones de la aplicación.',
        [
          {
            text: 'OK',
            onPress: () => navigation.replace('Dashboard'),
          },
        ]
      );
    } catch (error: any) {
      Alert.alert('Error', error.message);
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
          <Text style={styles.formTitle}>Cambiar Contraseña</Text>
          <Text style={styles.subtitle}>
            Tu contraseña es temporal. Por seguridad, debes cambiarla antes de continuar.
          </Text>

          <View style={styles.alertBox}>
            <Text style={styles.alertText}>
              ℹ️ La nueva contraseña debe cumplir los requisitos de seguridad
            </Text>
          </View>

          {/* Campo de Contraseña Actual */}
          <View style={styles.inputSection}>
            <View style={styles.labelRow}>
              <Text style={styles.inputLabel}>Contraseña actual</Text>
            </View>
            <View
              style={[
                styles.inputContainer,
                oldPasswordFocused && styles.inputContainerFocused,
                oldPasswordError && styles.inputContainerError,
              ]}>
              <Text style={styles.inputIcon}>🔒</Text>
              <TextInput
                style={styles.input}
                placeholder="••••••••"
                value={oldPassword}
                onChangeText={setOldPassword}
                onFocus={() => setOldPasswordFocused(true)}
                onBlur={() => setOldPasswordFocused(false)}
                secureTextEntry={!showOldPassword}
                autoCapitalize="none"
                editable={!loading}
                placeholderTextColor={colors.mediumGreen}
              />
              <TouchableOpacity
                onPress={() => setShowOldPassword(!showOldPassword)}
                style={styles.eyeButton}
                disabled={loading}>
                <Text style={styles.eyeText}>
                  {showOldPassword ? 'Ocultar' : 'Mostrar'}
                </Text>
              </TouchableOpacity>
            </View>
            {oldPasswordError ? (
              <Text style={styles.errorText}>{oldPasswordError}</Text>
            ) : null}
          </View>

          {/* Campo de Nueva Contraseña */}
          <View style={styles.inputSection}>
            <View style={styles.labelRow}>
              <Text style={styles.inputLabel}>Nueva contraseña</Text>
            </View>
            <View
              style={[
                styles.inputContainer,
                newPasswordFocused && styles.inputContainerFocused,
                newPasswordError && styles.inputContainerError,
              ]}>
              <Text style={styles.inputIcon}>🔒</Text>
              <TextInput
                style={styles.input}
                placeholder="••••••••"
                value={newPassword}
                onChangeText={text => {
                  setNewPassword(text);
                  checkPasswordStrength(text);
                }}
                onFocus={() => setNewPasswordFocused(true)}
                onBlur={() => setNewPasswordFocused(false)}
                secureTextEntry={!showNewPassword}
                autoCapitalize="none"
                editable={!loading}
                placeholderTextColor={colors.mediumGreen}
              />
              <TouchableOpacity
                onPress={() => setShowNewPassword(!showNewPassword)}
                style={styles.eyeButton}
                disabled={loading}>
                <Text style={styles.eyeText}>
                  {showNewPassword ? 'Ocultar' : 'Mostrar'}
                </Text>
              </TouchableOpacity>
            </View>
            {newPasswordError ? (
              <Text style={styles.errorText}>{newPasswordError}</Text>
            ) : null}

            {/* Medidor de seguridad de contraseña */}
            {newPassword.length > 0 && (
              <View style={styles.passwordStrengthContainer}>
                <Text style={styles.strengthText}>
                  Seguridad de la contraseña:{' '}
                {passwordStrength.score === 0
                  ? 'Muy débil'
                  : passwordStrength.score === 1
                  ? 'Débil'
                  : passwordStrength.score === 2
                  ? 'Media'
                  : passwordStrength.score === 3
                  ? 'Fuerte'
                  : 'Muy fuerte'}
              </Text>

            <View style={styles.strengthMeter}>
              <View
                style={[
                  styles.strengthIndicator,
                  {
                    width: `${(passwordStrength.score / 5) * 100}%`,
                    backgroundColor:
                      passwordStrength.score <= 1
                        ? '#ff4757'
                        : passwordStrength.score === 2
                        ? '#ffa502'
                        : passwordStrength.score === 3
                        ? '#2ed573'
                        : '#20bf6b',
                  },
                ]}
              />
            </View>

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
          )}
          </View>
            
          {/* Campo de Confirmar Contraseña */}
          <View style={styles.inputSection}>
            <View style={styles.labelRow}>
              <Text style={styles.inputLabel}>Confirmar nueva contraseña</Text>
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
                onChangeText={setConfirmPassword}
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
                    newPassword === confirmPassword
                      ? styles.passwordMatchSuccess
                      : styles.passwordMatchError,
                  ]}>
                  <Text style={styles.passwordMatchIcon}>
                    {newPassword === confirmPassword ? '✓' : '×'}
                  </Text>
                </View>
                <Text
                  style={[
                    styles.passwordMatchText,
                    newPassword === confirmPassword
                      ? styles.passwordMatchTextSuccess
                      : styles.passwordMatchTextError,
                  ]}>
                  {newPassword === confirmPassword
                    ? 'Las contraseñas coinciden'
                    : 'Las contraseñas no coinciden'}
                </Text>
              </View>
            ) : null}
          </View>

          {/* Botón de cambiar contraseña con animación */}
          <Pressable
            onPressIn={handlePressIn}
            onPressOut={handlePressOut}
            onPress={handleChangePassword}
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
                  <Text style={styles.buttonText}>Cambiar Contraseña</Text>
                  <Text style={styles.buttonIcon}>→</Text>
                </View>
              )}
            </Animated.View>
          </Pressable>
        </View>
      </ScrollView>
    </KeyboardAvoidingView>
  );
};

export default ChangePasswordScreen;
