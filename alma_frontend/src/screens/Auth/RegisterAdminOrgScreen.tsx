import React, {useState} from 'react';
import {
  KeyboardAvoidingView,
  Platform,
  ScrollView,
  Alert,
  ActivityIndicator,
  Text,
  View,
} from 'react-native';
import authService from '../../services/authService';
import {isValidDNI} from '../../utils/validation';
import Icon from 'react-native-vector-icons/MaterialCommunityIcons';
import {
  Container,
  Header,
  HeartIcon,
  Title,
  InputCard,
  InputLabel,
  InputWrapper,
  StyledInput,
  DisabledInput,
  RegisterButton,
  ButtonText,
  BackButton,
  BackButtonText,
  ErrorText,
  PasswordStrengthContainer,
  StrengthMeter,
  StrengthIndicator,
  RequirementText,
  RequirementMetText,
  theme,
} from '../../styles/screens/Auth/RegisterAdminOrgScreen.styles';

const RegisterAdminOrgScreen = ({route, navigation}: any) => {
  const {organizationData} = route.params;

  const [dni, setDni] = useState('');
  const [nombre, setNombre] = useState('');
  const [apellidos, setApellidos] = useState('');
  const [telefono, setTelefono] = useState('');
  const [password, setPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');

  const [errors, setErrors] = useState<any>({});
  const [loading, setLoading] = useState(false);
  const [showPassword, setShowPassword] = useState(false);
  const [showConfirmPassword, setShowConfirmPassword] = useState(false);

  const [passwordStrength, setPasswordStrength] = useState({
    score: 0,
    hasMinLength: false,
    hasUpperCase: false,
    hasLowerCase: false,
    hasNumber: false,
    hasSpecialChar: false,
  });

  const checkPasswordStrength = (password: string) => {
    const strength = {
      score: 0,
      hasMinLength: password.length >= 8,
      hasUpperCase: /[A-Z]/.test(password),
      hasLowerCase: /[a-z]/.test(password),
      hasNumber: /[0-9]/.test(password),
      hasSpecialChar: /[!@#$%^&*(),.?":{}|<>]/.test(password),
    };
    if (strength.hasMinLength) strength.score += 20;
    if (strength.hasUpperCase) strength.score += 20;
    if (strength.hasLowerCase) strength.score += 20;
    if (strength.hasNumber) strength.score += 20;
    if (strength.hasSpecialChar) strength.score += 20;
    return strength;
  };

  const handlePasswordChange = (text: string) => {
    setPassword(text);
    setPasswordStrength(checkPasswordStrength(text));
  };

  const validate = () => {
    const newErrors: any = {};
    if (!dni.trim()) newErrors.dni = 'El DNI/NIE es obligatorio';
    else if (!isValidDNI(dni)) newErrors.dni = 'El DNI/NIE no es válido';
    if (!nombre.trim()) newErrors.nombre = 'El nombre es obligatorio';
    if (!apellidos.trim()) newErrors.apellidos = 'Los apellidos son obligatorios';
    if (!telefono.trim()) newErrors.telefono = 'El teléfono es obligatorio';
    if (!password) newErrors.password = 'La contraseña es obligatoria';
    else if (password.length < 8) newErrors.password = 'La contraseña debe tener al menos 8 caracteres';
    if (!confirmPassword) newErrors.confirmPassword = 'Debe confirmar la contraseña';
    else if (password !== confirmPassword) newErrors.confirmPassword = 'Las contraseñas no coinciden';

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleRegister = async () => {
    if (!validate()) return;

    setLoading(true);

    const finalPayload = {
      ...organizationData,
      administrador: {
        dni: dni.toUpperCase().trim(),
        nombre: nombre.trim(),
        apellidos: apellidos.trim(),
        email: organizationData.emailCorporativo,
        telefono: telefono.trim(),
        cargo: 'Administrador',
        password,
      },
    };

    try {
      // Here you would handle the file uploads to AWS and get the URLs
      // For now, we are sending placeholder names
      finalPayload.documento_cif_url = organizationData.docCifName ? `s3://bucket-name/${organizationData.docCifName}` : null;
      finalPayload.documento_seguridad_social_url = organizationData.docSegSocialName ? `s3://bucket-name/${organizationData.docSegSocialName}` : null;

      await authService.registerOrganization(finalPayload);
      Alert.alert('¡Registro completado!', 'La organización y el administrador han sido registrados. Ahora serás redirigido para iniciar sesión.', [
        {text: 'Iniciar Sesión', onPress: () => navigation.reset({ index: 0, routes: [{ name: 'Login' }] })},
      ]);
    } catch (error: any) {
      Alert.alert('Error de registro', error.message);
    } finally {
      setLoading(false);
    }
  };

  const renderPasswordRequirements = () => (
    <PasswordStrengthContainer>
        <StrengthMeter>
            <StrengthIndicator style={{width: `${passwordStrength.score}%`, backgroundColor: passwordStrength.score < 40 ? '#e74c3c' : passwordStrength.score < 80 ? '#f1c40f' : '#2ecc71'}} />
        </StrengthMeter>
        {passwordStrength.hasMinLength ? <RequirementMetText>✓ Mínimo 8 caracteres</RequirementMetText> : <RequirementText>✗ Mínimo 8 caracteres</RequirementText>}
        {passwordStrength.hasUpperCase ? <RequirementMetText>✓ Al menos una mayúscula</RequirementMetText> : <RequirementText>✗ Al menos una mayúscula</RequirementText>}
        {passwordStrength.hasLowerCase ? <RequirementMetText>✓ Al menos una minúscula</RequirementMetText> : <RequirementText>✗ Al menos una minúscula</RequirementText>}
        {passwordStrength.hasNumber ? <RequirementMetText>✓ Al menos un número</RequirementMetText> : <RequirementText>✗ Al menos un número</RequirementText>}
        {passwordStrength.hasSpecialChar ? <RequirementMetText>✓ Al menos un carácter especial</RequirementMetText> : <RequirementText>✗ Al menos un carácter especial</RequirementText>}
    </PasswordStrengthContainer>
  );

  return (
    <KeyboardAvoidingView behavior={Platform.OS === 'ios' ? 'padding' : 'height'} style={{flex: 1}}>
      <Container>
        <ScrollView contentContainerStyle={{padding: 24}} keyboardShouldPersistTaps="handled">
          <Header>
            <HeartIcon source={require('../../assets/images/alma_logo.png')} resizeMode="contain" />
          </Header>
          <Title>Datos del Administrador</Title>

          <InputCard>
            <InputLabel>Email (de la organización)</InputLabel>
            <InputWrapper>
              <Icon name="email-check-outline" size={20} color={theme.green} />
              <DisabledInput value={organizationData.emailCorporativo} editable={false} />
            </InputWrapper>
          </InputCard>

          <InputCard>
            <InputLabel>DNI/NIE</InputLabel>
            <InputWrapper>
              <Icon name="card-account-details-outline" size={20} color={theme.green} />
              <StyledInput value={dni} onChangeText={setDni} maxLength={9} autoCapitalize="characters" />
            </InputWrapper>
            {errors.dni && <ErrorText>{errors.dni}</ErrorText>}
          </InputCard>

          <InputCard>
            <InputLabel>Nombre y Apellidos</InputLabel>
            <InputWrapper>
              <Icon name="account-outline" size={20} color={theme.green} />
              <StyledInput placeholder="Nombre" value={nombre} onChangeText={setNombre} />
            </InputWrapper>
            <InputWrapper style={{marginTop: 8}}>
                <Icon name="account-outline" size={20} color={'transparent'} />
                <StyledInput placeholder="Apellidos" value={apellidos} onChangeText={setApellidos} />
            </InputWrapper>
            {errors.nombre && <ErrorText>{errors.nombre}</ErrorText>}
            {errors.apellidos && <ErrorText>{errors.apellidos}</ErrorText>}
          </InputCard>

          <InputCard>
            <InputLabel>Teléfono</InputLabel>
            <InputWrapper>
              <Icon name="phone-outline" size={20} color={theme.green} />
              <StyledInput value={telefono} onChangeText={setTelefono} keyboardType="phone-pad" />
            </InputWrapper>
            {errors.telefono && <ErrorText>{errors.telefono}</ErrorText>}
          </InputCard>

          <InputCard>
            <InputLabel>Contraseña</InputLabel>
            <InputWrapper>
              <Icon name="lock-outline" size={20} color={theme.green} />
              <StyledInput placeholder="••••••••" value={password} onChangeText={handlePasswordChange} secureTextEntry={!showPassword} />
              <Icon name={showPassword ? 'eye-off-outline' : 'eye-outline'} size={24} color={theme.green} onPress={() => setShowPassword(!showPassword)} />
            </InputWrapper>
            {errors.password && <ErrorText>{errors.password}</ErrorText>}
            {password.length > 0 && renderPasswordRequirements()}
          </InputCard>

          <InputCard>
            <InputLabel>Confirmar Contraseña</InputLabel>
            <InputWrapper>
              <Icon name="lock-check-outline" size={20} color={theme.green} />
              <StyledInput placeholder="••••••••" value={confirmPassword} onChangeText={setConfirmPassword} secureTextEntry={!showConfirmPassword} />
              <Icon name={showConfirmPassword ? 'eye-off-outline' : 'eye-outline'} size={24} color={theme.green} onPress={() => setShowConfirmPassword(!showConfirmPassword)} />
            </InputWrapper>
            {errors.confirmPassword && <ErrorText>{errors.confirmPassword}</ErrorText>}
            {confirmPassword.length > 0 && password !== confirmPassword && <ErrorText>Las contraseñas no coinciden</ErrorText>}
            {confirmPassword.length > 0 && password === confirmPassword && <RequirementMetText style={{marginTop: 4}}>✓ Las contraseñas coinciden</RequirementMetText>}
          </InputCard>

          <RegisterButton onPress={handleRegister} disabled={loading}>
            {loading ? <ActivityIndicator color={theme.black} /> : <ButtonText>Registrar</ButtonText>}
          </RegisterButton>

          <BackButton onPress={() => navigation.goBack()} disabled={loading}>
            <BackButtonText>Volver</BackButtonText>
          </BackButton>
        </ScrollView>
      </Container>
    </KeyboardAvoidingView>
  );
};

export default RegisterAdminOrgScreen;
