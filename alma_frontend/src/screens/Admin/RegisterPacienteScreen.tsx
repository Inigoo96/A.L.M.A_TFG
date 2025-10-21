import React, {useState} from 'react';
import {
  View,
  Text,
  TextInput,
  TouchableOpacity,
  StyleSheet,
  ScrollView,
  Alert,
  ActivityIndicator,
  KeyboardAvoidingView,
  Platform,
} from 'react-native';
import {Picker} from '@react-native-picker/picker';
import {colors, fontSize, spacing, borderRadius} from '../../theme';
import {PacienteRegistroDTO, Genero} from '../../types/api.types';
import usuarioService from '../../services/usuarioService';
import {
  isValidEmail,
  isValidDNI,
  isValidTelefono,
  isValidFechaNacimiento,
} from '../../utils/validation';

interface Props {
  navigation: any;
}

const RegisterPacienteScreen: React.FC<Props> = ({navigation}) => {
  const [loading, setLoading] = useState(false);

  // Estados del formulario
  const [nombre, setNombre] = useState('');
  const [apellidos, setApellidos] = useState('');
  const [email, setEmail] = useState('');
  const [dni, setDni] = useState('');
  const [telefono, setTelefono] = useState('');
  const [tarjetaSanitaria, setTarjetaSanitaria] = useState('');
  const [fechaNacimiento, setFechaNacimiento] = useState('');
  const [genero, setGenero] = useState<Genero | ''>('');

  // Estados de validación
  const [errors, setErrors] = useState<{[key: string]: string}>({});

  const validateForm = (): boolean => {
    const newErrors: {[key: string]: string} = {};

    // Campos obligatorios
    if (!nombre.trim()) {
      newErrors.nombre = 'El nombre es obligatorio';
    }

    if (!apellidos.trim()) {
      newErrors.apellidos = 'Los apellidos son obligatorios';
    }

    if (!email.trim()) {
      newErrors.email = 'El email es obligatorio';
    } else if (!isValidEmail(email)) {
      newErrors.email = 'El email no es válido';
    }

    // DNI opcional pero si se proporciona debe ser válido
    if (dni.trim() && !isValidDNI(dni)) {
      newErrors.dni = 'El DNI/NIE no es válido';
    }

    // Teléfono opcional pero si se proporciona debe ser válido
    if (telefono.trim() && !isValidTelefono(telefono)) {
      newErrors.telefono = 'El teléfono no es válido (formato español)';
    }

    // Fecha de nacimiento opcional pero si se proporciona debe ser válida
    if (fechaNacimiento.trim() && !isValidFechaNacimiento(fechaNacimiento)) {
      newErrors.fechaNacimiento =
        'La fecha de nacimiento no es válida (formato: AAAA-MM-DD)';
    }

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleRegister = async () => {
    if (!validateForm()) {
      Alert.alert(
        'Error de validación',
        'Por favor, corrige los errores en el formulario',
      );
      return;
    }

    setLoading(true);

    try {
      const data: PacienteRegistroDTO = {
        nombre: nombre.trim(),
        apellidos: apellidos.trim(),
        email: email.trim().toLowerCase(),
        dni: dni.trim() || undefined,
        telefono: telefono.trim() || undefined,
        tarjetaSanitaria: tarjetaSanitaria.trim() || undefined,
        fechaNacimiento: fechaNacimiento.trim() || undefined,
        genero: genero || undefined,
      };

      const paciente = await usuarioService.registerPaciente(data);

      Alert.alert(
        '✅ Paciente Registrado',
        `Se ha registrado exitosamente a:\n\n` +
          `${paciente.nombre} ${paciente.apellidos}\n` +
          `Email: ${paciente.email}\n\n` +
          `⚠️ IMPORTANTE: Se ha generado una contraseña temporal.\n` +
          `El paciente debe cambiarla en su primer acceso.`,
        [
          {
            text: 'Entendido',
            onPress: () => navigation.goBack(),
          },
        ],
      );
    } catch (error: any) {
      console.error('Error al registrar paciente:', error);
      Alert.alert(
        'Error',
        error.message || 'No se pudo registrar el paciente',
      );
    } finally {
      setLoading(false);
    }
  };

  const handleCancel = () => {
    Alert.alert(
      'Cancelar registro',
      '¿Estás seguro de que deseas cancelar? Se perderán los datos ingresados.',
      [
        {text: 'Continuar editando', style: 'cancel'},
        {
          text: 'Sí, cancelar',
          style: 'destructive',
          onPress: () => navigation.goBack(),
        },
      ],
    );
  };

  return (
    <KeyboardAvoidingView
      style={styles.container}
      behavior={Platform.OS === 'ios' ? 'padding' : 'height'}>
      <ScrollView
        contentContainerStyle={styles.scrollContent}
        keyboardShouldPersistTaps="handled">
        {/* Header */}
        <View style={styles.header}>
          <Text style={styles.title}>Registrar Paciente</Text>
          <Text style={styles.subtitle}>
            Complete los datos del nuevo paciente
          </Text>
        </View>

        {/* Datos Personales */}
        <View style={styles.section}>
          <Text style={styles.sectionTitle}>📋 Datos Personales</Text>

          <View style={styles.inputGroup}>
            <Text style={styles.label}>
              Nombre <Text style={styles.required}>*</Text>
            </Text>
            <TextInput
              style={[styles.input, errors.nombre && styles.inputError]}
              value={nombre}
              onChangeText={setNombre}
              placeholder="Ej: María"
              autoCapitalize="words"
            />
            {errors.nombre && (
              <Text style={styles.errorText}>{errors.nombre}</Text>
            )}
          </View>

          <View style={styles.inputGroup}>
            <Text style={styles.label}>
              Apellidos <Text style={styles.required}>*</Text>
            </Text>
            <TextInput
              style={[styles.input, errors.apellidos && styles.inputError]}
              value={apellidos}
              onChangeText={setApellidos}
              placeholder="Ej: Pérez González"
              autoCapitalize="words"
            />
            {errors.apellidos && (
              <Text style={styles.errorText}>{errors.apellidos}</Text>
            )}
          </View>

          <View style={styles.inputGroup}>
            <Text style={styles.label}>DNI/NIE (opcional)</Text>
            <TextInput
              style={[styles.input, errors.dni && styles.inputError]}
              value={dni}
              onChangeText={setDni}
              placeholder="Ej: 12345678Z"
              autoCapitalize="characters"
              maxLength={9}
            />
            {errors.dni && <Text style={styles.errorText}>{errors.dni}</Text>}
          </View>

          <View style={styles.inputGroup}>
            <Text style={styles.label}>Fecha de Nacimiento (opcional)</Text>
            <TextInput
              style={[
                styles.input,
                errors.fechaNacimiento && styles.inputError,
              ]}
              value={fechaNacimiento}
              onChangeText={setFechaNacimiento}
              placeholder="AAAA-MM-DD (Ej: 1990-05-15)"
              keyboardType="numbers-and-punctuation"
            />
            {errors.fechaNacimiento && (
              <Text style={styles.errorText}>{errors.fechaNacimiento}</Text>
            )}
            <Text style={styles.helpText}>
              Formato: AAAA-MM-DD (ejemplo: 1990-05-15)
            </Text>
          </View>

          <View style={styles.inputGroup}>
            <Text style={styles.label}>Género (opcional)</Text>
            <View style={styles.pickerContainer}>
              <Picker
                selectedValue={genero}
                onValueChange={(itemValue) => setGenero(itemValue)}
                style={styles.picker}>
                <Picker.Item label="Seleccionar..." value="" />
                <Picker.Item label="Masculino" value={Genero.MASCULINO} />
                <Picker.Item label="Femenino" value={Genero.FEMENINO} />
                <Picker.Item label="No binario" value={Genero.NO_BINARIO} />
                <Picker.Item
                  label="Prefiero no decir"
                  value={Genero.PREFIERO_NO_DECIR}
                />
              </Picker>
            </View>
          </View>
        </View>

        {/* Datos de Contacto */}
        <View style={styles.section}>
          <Text style={styles.sectionTitle}>📞 Datos de Contacto</Text>

          <View style={styles.inputGroup}>
            <Text style={styles.label}>
              Email <Text style={styles.required}>*</Text>
            </Text>
            <TextInput
              style={[styles.input, errors.email && styles.inputError]}
              value={email}
              onChangeText={setEmail}
              placeholder="paciente@example.com"
              keyboardType="email-address"
              autoCapitalize="none"
              autoCorrect={false}
            />
            {errors.email && (
              <Text style={styles.errorText}>{errors.email}</Text>
            )}
          </View>

          <View style={styles.inputGroup}>
            <Text style={styles.label}>Teléfono (opcional)</Text>
            <TextInput
              style={[styles.input, errors.telefono && styles.inputError]}
              value={telefono}
              onChangeText={setTelefono}
              placeholder="Ej: 666777888"
              keyboardType="phone-pad"
            />
            {errors.telefono && (
              <Text style={styles.errorText}>{errors.telefono}</Text>
            )}
          </View>
        </View>

        {/* Datos Sanitarios */}
        <View style={styles.section}>
          <Text style={styles.sectionTitle}>🏥 Datos Sanitarios</Text>

          <View style={styles.inputGroup}>
            <Text style={styles.label}>Tarjeta Sanitaria (opcional)</Text>
            <TextInput
              style={styles.input}
              value={tarjetaSanitaria}
              onChangeText={setTarjetaSanitaria}
              placeholder="Ej: TSI123456789"
              autoCapitalize="characters"
            />
          </View>
        </View>

        {/* Información sobre contraseña */}
        <View style={styles.infoBox}>
          <Text style={styles.infoTitle}>ℹ️ Contraseña Temporal</Text>
          <Text style={styles.infoText}>
            Se generará automáticamente una contraseña temporal que el paciente
            deberá cambiar en su primer acceso.
          </Text>
        </View>

        {/* Botones */}
        <View style={styles.buttonContainer}>
          <TouchableOpacity
            style={[styles.button, styles.cancelButton]}
            onPress={handleCancel}
            disabled={loading}>
            <Text style={styles.cancelButtonText}>Cancelar</Text>
          </TouchableOpacity>

          <TouchableOpacity
            style={[
              styles.button,
              styles.submitButton,
              loading && styles.buttonDisabled,
            ]}
            onPress={handleRegister}
            disabled={loading}>
            {loading ? (
              <ActivityIndicator color={colors.white} />
            ) : (
              <Text style={styles.submitButtonText}>Registrar</Text>
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
    padding: spacing.lg,
    paddingBottom: spacing.xxl,
  },
  header: {
    marginBottom: spacing.xl,
  },
  title: {
    fontSize: fontSize.xxl,
    fontWeight: 'bold',
    color: colors.darkGreen,
    marginBottom: spacing.xs,
  },
  subtitle: {
    fontSize: fontSize.md,
    color: colors.mediumGreen,
  },
  section: {
    backgroundColor: colors.white,
    borderRadius: borderRadius.lg,
    padding: spacing.lg,
    marginBottom: spacing.lg,
    shadowColor: '#000',
    shadowOffset: {width: 0, height: 2},
    shadowOpacity: 0.1,
    shadowRadius: 4,
    elevation: 3,
  },
  sectionTitle: {
    fontSize: fontSize.lg,
    fontWeight: 'bold',
    color: colors.darkGreen,
    marginBottom: spacing.md,
  },
  inputGroup: {
    marginBottom: spacing.md,
  },
  label: {
    fontSize: fontSize.md,
    color: colors.textPrimary,
    marginBottom: spacing.xs,
    fontWeight: '600',
  },
  required: {
    color: colors.error,
  },
  input: {
    backgroundColor: colors.white,
    borderWidth: 1,
    borderColor: colors.border,
    borderRadius: borderRadius.md,
    padding: spacing.md,
    fontSize: fontSize.md,
    color: colors.textPrimary,
  },
  inputError: {
    borderColor: colors.error,
    borderWidth: 2,
  },
  errorText: {
    color: colors.error,
    fontSize: fontSize.sm,
    marginTop: spacing.xs,
  },
  helpText: {
    color: colors.textSecondary,
    fontSize: fontSize.sm,
    marginTop: spacing.xs,
    fontStyle: 'italic',
  },
  pickerContainer: {
    borderWidth: 1,
    borderColor: colors.border,
    borderRadius: borderRadius.md,
    backgroundColor: colors.white,
  },
  picker: {
    height: 50,
  },
  infoBox: {
    backgroundColor: colors.secondary,
    borderRadius: borderRadius.md,
    padding: spacing.md,
    marginBottom: spacing.lg,
    borderLeftWidth: 4,
    borderLeftColor: colors.primary,
  },
  infoTitle: {
    fontSize: fontSize.md,
    fontWeight: 'bold',
    color: colors.darkGreen,
    marginBottom: spacing.xs,
  },
  infoText: {
    fontSize: fontSize.sm,
    color: colors.textSecondary,
    lineHeight: 20,
  },
  buttonContainer: {
    flexDirection: 'row',
    gap: spacing.md,
  },
  button: {
    flex: 1,
    padding: spacing.md,
    borderRadius: borderRadius.md,
    alignItems: 'center',
    justifyContent: 'center',
    minHeight: 50,
  },
  cancelButton: {
    backgroundColor: colors.white,
    borderWidth: 1,
    borderColor: colors.border,
  },
  cancelButtonText: {
    color: colors.textPrimary,
    fontSize: fontSize.md,
    fontWeight: '600',
  },
  submitButton: {
    backgroundColor: colors.primary,
    shadowColor: colors.primary,
    shadowOffset: {width: 0, height: 2},
    shadowOpacity: 0.3,
    shadowRadius: 4,
    elevation: 4,
  },
  submitButtonText: {
    color: colors.white,
    fontSize: fontSize.md,
    fontWeight: 'bold',
  },
  buttonDisabled: {
    opacity: 0.6,
  },
});

export default RegisterPacienteScreen;
