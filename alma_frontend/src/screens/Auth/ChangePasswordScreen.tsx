import React, {useState} from 'react';
import {
  Alert,
  ActivityIndicator,
  KeyboardAvoidingView,
  Platform,
  ScrollView,
} from 'react-native';
import authService from '../../services/authService';
import AsyncStorage from '@react-native-async-storage/async-storage';
import Icon from 'react-native-vector-icons/MaterialCommunityIcons';
import {
  Container,
  Header,
  HeartIcon,
  Separator,
  Title,
  Subtitle,
  InputCard,
  InputLabel,
  InputWrapper,
  StyledInput,
  ChangeButton,
  ButtonText,
  ErrorText,
  PasswordStrengthContainer,
  StrengthMeter,
  StrengthIndicator,
  RequirementText,
  RequirementMetText,
  theme,
} from '../../styles/screens/Auth/ChangePasswordScreen.styles';

const ChangePasswordScreen = ({navigation}: any) => {
  const [oldPassword, setOldPassword] = useState('');
  const [newPassword, setNewPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');

  const [errors, setErrors] = useState<any>({});
  const [loading, setLoading] = useState(false);
  const [showOldPassword, setShowOldPassword] = useState(false);
  const [showNewPassword, setShowNewPassword] = useState(false);
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
    setNewPassword(text);
    setPasswordStrength(checkPasswordStrength(text));
  };

  const validate = () => {
    const newErrors: any = {};
    if (!oldPassword.trim()) newErrors.oldPassword = 'La contraseña actual es obligatoria';
    if (!newPassword) newErrors.newPassword = 'La nueva contraseña es obligatoria';
    else if (newPassword.length < 8) newErrors.newPassword = 'La contraseña debe tener al menos 8 caracteres';
    if (!confirmPassword) newErrors.confirmPassword = 'Debe confirmar la contraseña';
    else if (newPassword !== confirmPassword) newErrors.confirmPassword = 'Las contraseñas no coinciden';

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleChangePassword = async () => {
    if (!validate()) return;

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
    <KeyboardAvoidingView
      behavior={Platform.OS === 'ios' ? 'padding' : 'height'}
      style={{flex: 1}}>
      <Container>
        <Header>
          <HeartIcon source={require('../../assets/images/alma_logo.png')} resizeMode="contain" />
        </Header>
        <Separator />
        <ScrollView contentContainerStyle={{padding: 24}} keyboardShouldPersistTaps="handled">
          <Title>Cambiar Contraseña</Title>
          <Subtitle>
            Tu contraseña es temporal. Por seguridad, debes cambiarla antes de continuar.
          </Subtitle>

          <InputCard>
            <InputLabel>Contraseña actual</InputLabel>
            <InputWrapper>
              <Icon name="lock-outline" size={20} color={theme.green} />
              <StyledInput
                placeholder="••••••••"
                placeholderTextColor={theme.lightGreen}
                value={oldPassword}
                onChangeText={setOldPassword}
                secureTextEntry={!showOldPassword}
                editable={!loading}
              />
              <Icon
                name={showOldPassword ? 'eye-off-outline' : 'eye-outline'}
                size={24}
                color={theme.green}
                onPress={() => setShowOldPassword(!showOldPassword)}
              />
            </InputWrapper>
            {errors.oldPassword && <ErrorText>{errors.oldPassword}</ErrorText>}
          </InputCard>

          <InputCard>
            <InputLabel>Nueva contraseña</InputLabel>
            <InputWrapper>
              <Icon name="lock-outline" size={20} color={theme.green} />
              <StyledInput
                placeholder="••••••••"
                placeholderTextColor={theme.lightGreen}
                value={newPassword}
                onChangeText={handlePasswordChange}
                secureTextEntry={!showNewPassword}
                editable={!loading}
              />
              <Icon
                name={showNewPassword ? 'eye-off-outline' : 'eye-outline'}
                size={24}
                color={theme.green}
                onPress={() => setShowNewPassword(!showNewPassword)}
              />
            </InputWrapper>
            {errors.newPassword && <ErrorText>{errors.newPassword}</ErrorText>}
            {newPassword.length > 0 && renderPasswordRequirements()}
          </InputCard>

          <InputCard>
            <InputLabel>Confirmar nueva contraseña</InputLabel>
            <InputWrapper>
              <Icon name="lock-check-outline" size={20} color={theme.green} />
              <StyledInput
                placeholder="••••••••"
                placeholderTextColor={theme.lightGreen}
                value={confirmPassword}
                onChangeText={setConfirmPassword}
                secureTextEntry={!showConfirmPassword}
                editable={!loading}
              />
              <Icon
                name={showConfirmPassword ? 'eye-off-outline' : 'eye-outline'}
                size={24}
                color={theme.green}
                onPress={() => setShowConfirmPassword(!showConfirmPassword)}
              />
            </InputWrapper>
            {errors.confirmPassword && <ErrorText>{errors.confirmPassword}</ErrorText>}
            {confirmPassword.length > 0 && newPassword !== confirmPassword && <ErrorText>Las contraseñas no coinciden</ErrorText>}
            {confirmPassword.length > 0 && newPassword === confirmPassword && <RequirementMetText style={{marginTop: 4}}>✓ Las contraseñas coinciden</RequirementMetText>}
          </InputCard>

          <ChangeButton onPress={handleChangePassword} disabled={loading}>
            {loading ? <ActivityIndicator color={theme.black} /> : <ButtonText>Cambiar Contraseña</ButtonText>}
          </ChangeButton>
        </ScrollView>
      </Container>
    </KeyboardAvoidingView>
  );
};

export default ChangePasswordScreen;
