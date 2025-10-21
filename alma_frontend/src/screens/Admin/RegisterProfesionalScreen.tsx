import React, {useState} from 'react';
import {
  View,
  Text,
  TextInput,
  TouchableOpacity,
  ScrollView,
  Alert,
  ActivityIndicator,
  KeyboardAvoidingView,
  Platform,
} from 'react-native';
import {colors} from '../../theme';
import {ProfesionalRegistroDTO} from '../../types/api.types';
import usuarioService from '../../services/usuarioService';
import {isValidEmail, isValidDNI, isValidTelefono} from '../../utils/validation';
import {styles} from '../../styles/screens/Admin/RegisterProfesionalScreen.styles';

interface Props {
  navigation: any;
}

const RegisterProfesionalScreen: React.FC<Props> = ({navigation}) => {
  const [loading, setLoading] = useState(false);

  // Estados del formulario
  const [nombre, setNombre] = useState('');
  const [apellidos, setApellidos] = useState('');
  const [email, setEmail] = useState('');
  const [dni, setDni] = useState('');
  const [telefono, setTelefono] = useState('');
  const [numeroColegiado, setNumeroColegiado] = useState('');
  const [especialidad, setEspecialidad] = useState('');
  const [centroSalud, setCentroSalud] = useState('');

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

    // DNI obligatorio y debe ser válido
    if (!dni.trim()) {
      newErrors.dni = 'El DNI/NIE es obligatorio';
    } else if (!isValidDNI(dni)) {
      newErrors.dni = 'El DNI/NIE no es válido';
    }

    // Teléfono opcional pero si se proporciona debe ser válido
    if (telefono.trim() && !isValidTelefono(telefono)) {
      newErrors.telefono = 'El teléfono no es válido (formato español)';
    }

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleRegister = async () => {
    if (!validateForm()) {
      Alert.alert('Error de validación', 'Por favor, corrige los errores en el formulario');
      return;
    }

    setLoading(true);

    try {
      const data: ProfesionalRegistroDTO = {
        nombre: nombre.trim(),
        apellidos: apellidos.trim(),
        email: email.trim().toLowerCase(),
        dni: dni.trim(), // REQUERIDO en backend
        telefono: telefono.trim() || undefined,
        numeroColegiado: numeroColegiado.trim() || undefined,
        especialidad: especialidad.trim() || undefined,
        centroSalud: centroSalud.trim() || undefined,
      };

      const profesional = await usuarioService.registerProfesional(data);

      Alert.alert(
        '✅ Profesional Registrado',
        `Se ha registrado exitosamente a:\n\n` +
          `${profesional.nombre} ${profesional.apellidos}\n` +
          `Email: ${profesional.email}\n\n` +
          `⚠️ IMPORTANTE: Se ha generado una contraseña temporal.\n` +
          `El profesional debe cambiarla en su primer acceso.`,
        [
          {
            text: 'Entendido',
            onPress: () => navigation.goBack(),
          },
        ],
      );
    } catch (error: any) {
      console.error('Error al registrar profesional:', error);
      Alert.alert('Error', error.message || 'No se pudo registrar el profesional');
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
          <Text style={styles.title}>Registrar Profesional</Text>
          <Text style={styles.subtitle}>
            Complete los datos del nuevo profesional
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
              placeholder="Ej: Juan"
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
              placeholder="Ej: García López"
              autoCapitalize="words"
            />
            {errors.apellidos && (
              <Text style={styles.errorText}>{errors.apellidos}</Text>
            )}
          </View>

          <View style={styles.inputGroup}>
            <Text style={styles.label}>
              DNI/NIE <Text style={styles.required}>*</Text>
            </Text>
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
              placeholder="profesional@example.com"
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

        {/* Datos Profesionales */}
        <View style={styles.section}>
          <Text style={styles.sectionTitle}>👨‍⚕️ Datos Profesionales</Text>

          <View style={styles.inputGroup}>
            <Text style={styles.label}>Número Colegiado (opcional)</Text>
            <TextInput
              style={styles.input}
              value={numeroColegiado}
              onChangeText={setNumeroColegiado}
              placeholder="Ej: M12345"
              autoCapitalize="characters"
            />
          </View>

          <View style={styles.inputGroup}>
            <Text style={styles.label}>Especialidad (opcional)</Text>
            <TextInput
              style={styles.input}
              value={especialidad}
              onChangeText={setEspecialidad}
              placeholder="Ej: Psicología Clínica"
              autoCapitalize="words"
            />
          </View>

          <View style={styles.inputGroup}>
            <Text style={styles.label}>Centro de Salud (opcional)</Text>
            <TextInput
              style={styles.input}
              value={centroSalud}
              onChangeText={setCentroSalud}
              placeholder="Ej: Centro de Salud Norte"
              autoCapitalize="words"
            />
          </View>
        </View>

        {/* Información sobre contraseña */}
        <View style={styles.infoBox}>
          <Text style={styles.infoTitle}>ℹ️ Contraseña Temporal</Text>
          <Text style={styles.infoText}>
            Se generará automáticamente una contraseña temporal que el profesional
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
            style={[styles.button, styles.submitButton, loading && styles.buttonDisabled]}
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

export default RegisterProfesionalScreen;
