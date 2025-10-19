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

const RegisterOrganizationScreen = ({navigation}: any) => {
  const [nombreOrganizacion, setNombreOrganizacion] = useState('');
  const [cif, setCif] = useState('');
  const [nombre, setNombre] = useState('');
  const [apellidos, setApellidos] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [loading, setLoading] = useState(false);

  const validateCIF = (cif: string): boolean => {
    const cifRegex = /^[A-Z][0-9]{8}$/;
    return cifRegex.test(cif.toUpperCase());
  };

  const handleRegister = async () => {
    // Validaciones
    if (
      !nombreOrganizacion.trim() ||
      !cif.trim() ||
      !nombre.trim() ||
      !apellidos.trim() ||
      !email.trim() ||
      !password.trim() ||
      !confirmPassword.trim()
    ) {
      Alert.alert('Error', 'Por favor, completa todos los campos');
      return;
    }

    if (!validateCIF(cif)) {
      Alert.alert(
        'CIF inválido',
        'El CIF debe tener el formato correcto (ej: A12345678)'
      );
      return;
    }

    if (password.length < 8) {
      Alert.alert('Error', 'La contraseña debe tener al menos 8 caracteres');
      return;
    }

    if (password !== confirmPassword) {
      Alert.alert('Error', 'Las contraseñas no coinciden');
      return;
    }

    setLoading(true);

    try {
      const response = await authService.registerOrganization({
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
      <ScrollView contentContainerStyle={styles.scrollContent}>
        <View style={styles.formContainer}>
          <Text style={styles.title}>Registro de Organización</Text>
          <Text style={styles.subtitle}>
            Complete los datos para registrar su organización
          </Text>

          {/* Sección Organización */}
          <View style={styles.section}>
            <Text style={styles.sectionTitle}>Datos de la Organización</Text>

            <TextInput
              style={styles.input}
              placeholder="Nombre de la organización"
              value={nombreOrganizacion}
              onChangeText={setNombreOrganizacion}
              autoCapitalize="words"
              editable={!loading}
              placeholderTextColor={colors.mediumGreen}
            />

            <TextInput
              style={styles.input}
              placeholder="CIF (ej: A12345678)"
              value={cif}
              onChangeText={setCif}
              autoCapitalize="characters"
              maxLength={9}
              editable={!loading}
              placeholderTextColor={colors.mediumGreen}
            />
          </View>

          {/* Sección Administrador */}
          <View style={styles.section}>
            <Text style={styles.sectionTitle}>Datos del Administrador</Text>

            <TextInput
              style={styles.input}
              placeholder="Nombre"
              value={nombre}
              onChangeText={setNombre}
              autoCapitalize="words"
              editable={!loading}
              placeholderTextColor={colors.mediumGreen}
            />

            <TextInput
              style={styles.input}
              placeholder="Apellidos"
              value={apellidos}
              onChangeText={setApellidos}
              autoCapitalize="words"
              editable={!loading}
              placeholderTextColor={colors.mediumGreen}
            />

            <TextInput
              style={styles.input}
              placeholder="Correo electrónico"
              value={email}
              onChangeText={setEmail}
              keyboardType="email-address"
              autoCapitalize="none"
              autoCorrect={false}
              editable={!loading}
              placeholderTextColor={colors.mediumGreen}
            />

            <TextInput
              style={styles.input}
              placeholder="Contraseña (mín. 8 caracteres)"
              value={password}
              onChangeText={setPassword}
              secureTextEntry
              autoCapitalize="none"
              editable={!loading}
              placeholderTextColor={colors.mediumGreen}
            />

            <TextInput
              style={styles.input}
              placeholder="Confirmar contraseña"
              value={confirmPassword}
              onChangeText={setConfirmPassword}
              secureTextEntry
              autoCapitalize="none"
              editable={!loading}
              placeholderTextColor={colors.mediumGreen}
            />
          </View>

          <TouchableOpacity
            style={[styles.button, loading && styles.buttonDisabled]}
            onPress={handleRegister}
            disabled={loading}>
            {loading ? (
              <ActivityIndicator color={colors.white} />
            ) : (
              <Text style={styles.buttonText}>Registrar Organización</Text>
            )}
          </TouchableOpacity>

          <TouchableOpacity
            style={styles.linkButton}
            onPress={() => navigation.goBack()}
            disabled={loading}>
            <Text style={styles.linkText}>Volver</Text>
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
    padding: spacing.lg,
  },
  formContainer: {
    backgroundColor: colors.white,
    borderRadius: borderRadius.lg,
    padding: spacing.lg,
    shadowColor: '#000',
    shadowOffset: {width: 0, height: 2},
    shadowOpacity: 0.1,
    shadowRadius: 8,
    elevation: 4,
    marginVertical: spacing.md,
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
    marginBottom: spacing.lg,
  },
  section: {
    marginBottom: spacing.lg,
  },
  sectionTitle: {
    fontSize: fontSize.md,
    fontWeight: '600',
    color: colors.darkGreen,
    marginBottom: spacing.md,
    borderLeftWidth: 4,
    borderLeftColor: colors.primary,
    paddingLeft: spacing.sm,
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
  linkButton: {
    marginTop: spacing.md,
    alignItems: 'center',
  },
  linkText: {
    color: colors.primary,
    fontSize: fontSize.md,
    fontWeight: '600',
  },
});

export default RegisterOrganizationScreen;