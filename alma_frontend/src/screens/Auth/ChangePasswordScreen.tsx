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
} from 'react-native';
import authService from '../../services/authService';
import {colors, fontSize, spacing, borderRadius} from '../../theme';

const ChangePasswordScreen = ({navigation}: any) => {
  const [oldPassword, setOldPassword] = useState('');
  const [newPassword, setNewPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [loading, setLoading] = useState(false);

  const handleChangePassword = async () => {
    if (!oldPassword.trim() || !newPassword.trim() || !confirmPassword.trim()) {
      Alert.alert('Error', 'Por favor, completa todos los campos');
      return;
    }

    if (newPassword !== confirmPassword) {
      Alert.alert('Error', 'Las contraseñas nuevas no coinciden');
      return;
    }

    if (newPassword.length < 6) {
      Alert.alert('Error', 'La contraseña debe tener al menos 6 caracteres');
      return;
    }

    setLoading(true);

    try {
      await authService.updatePassword(oldPassword, newPassword);

      Alert.alert(
        'Éxito',
        'Contraseña actualizada correctamente',
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
      <ScrollView contentContainerStyle={styles.scrollContent}>
        <View style={styles.formContainer}>
          <View style={styles.header}>
            <Text style={styles.title}>Cambiar Contraseña</Text>
            <Text style={styles.subtitle}>
              Tu contraseña es temporal. Por seguridad, debes cambiarla antes de continuar.
            </Text>
          </View>

          <View style={styles.alertBox}>
            <Text style={styles.alertText}>
              ℹ️ La nueva contraseña debe tener al menos 6 caracteres
            </Text>
          </View>

          <TextInput
            style={styles.input}
            placeholder="Contraseña actual"
            value={oldPassword}
            onChangeText={setOldPassword}
            secureTextEntry
            autoCapitalize="none"
            editable={!loading}
            placeholderTextColor={colors.mediumGreen}
          />

          <TextInput
            style={styles.input}
            placeholder="Nueva contraseña"
            value={newPassword}
            onChangeText={setNewPassword}
            secureTextEntry
            autoCapitalize="none"
            editable={!loading}
            placeholderTextColor={colors.mediumGreen}
          />

          <TextInput
            style={styles.input}
            placeholder="Confirmar nueva contraseña"
            value={confirmPassword}
            onChangeText={setConfirmPassword}
            secureTextEntry
            autoCapitalize="none"
            editable={!loading}
            placeholderTextColor={colors.mediumGreen}
          />

          <TouchableOpacity
            style={[styles.button, loading && styles.buttonDisabled]}
            onPress={handleChangePassword}
            disabled={loading}>
            {loading ? (
              <ActivityIndicator color={colors.white} />
            ) : (
              <Text style={styles.buttonText}>Cambiar Contraseña</Text>
            )}
          </TouchableOpacity>
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
  formContainer: {
    backgroundColor: colors.white,
    borderRadius: borderRadius.lg,
    padding: spacing.xl,
    shadowColor: '#000',
    shadowOffset: {width: 0, height: 2},
    shadowOpacity: 0.1,
    shadowRadius: 8,
    elevation: 4,
    borderLeftWidth: 4,
    borderLeftColor: colors.secondary,
  },
  header: {
    marginBottom: spacing.lg,
  },
  title: {
    fontSize: fontSize.xl,
    fontWeight: 'bold',
    color: colors.darkGreen,
    textAlign: 'center',
    marginBottom: spacing.sm,
  },
  subtitle: {
    fontSize: fontSize.sm,
    color: colors.mediumGreen,
    textAlign: 'center',
    lineHeight: 20,
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
  input: {
    backgroundColor: colors.inputBackground,
    borderWidth: 1,
    borderColor: colors.border,
    borderRadius: borderRadius.sm,
    padding: spacing.md,
    marginBottom: spacing.md,
    fontSize: fontSize.md,
    color: colors.textPrimary,
  },
  button: {
    backgroundColor: colors.primary,
    padding: spacing.md,
    borderRadius: borderRadius.sm,
    alignItems: 'center',
    marginTop: spacing.md,
    shadowColor: colors.shadowMedium,
    shadowOffset: {width: 0, height: 2},
    shadowOpacity: 0.1,
    shadowRadius: 4,
    elevation: 3,
  },
  buttonDisabled: {
    backgroundColor: colors.mediumGreen,
    opacity: 0.6,
  },
  buttonText: {
    color: colors.white,
    fontSize: fontSize.md,
    fontWeight: 'bold',
  },
});

export default ChangePasswordScreen;