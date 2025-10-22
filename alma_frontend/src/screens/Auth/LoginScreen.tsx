import React, {useState} from 'react';
import {
  Alert,
  ActivityIndicator,
  KeyboardAvoidingView,
  Platform,
  ScrollView,
  TouchableOpacity,
} from 'react-native';
import authService from '../../services/authService';
import {
  Container,
  Header,
  Logo,
  Separator,
  ContentContainer,
  Title,
  InputCard,
  InputLabel,
  InputWrapper,
  StyledInput,
  InputIcon,
  ForgotPasswordText,
  AccessButton,
  AccessButtonText,
  FooterText,
  BackButton,
  BackButtonText,
  theme,
} from '../../styles/screens/Auth/LoginScreen.styles';

const LoginScreen = ({navigation}: any) => {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [loading, setLoading] = useState(false);
  const [showPassword, setShowPassword] = useState(false);

  const handleLogin = async () => {
    if (!email.trim() || !password.trim()) {
      Alert.alert('Campos incompletos', 'Por favor, ingresa tu email y contraseña.');
      return;
    }
    setLoading(true);
    try {
      await authService.login(email.trim(), password);
      navigation.replace('Dashboard');
    } catch (error: any) {
      Alert.alert('Error de autenticación', error.message);
    } finally {
      setLoading(false);
    }
  };

  return (
    <Container>
      <Header>
        <Logo source={require('../../assets/images/alma_logo.png')} resizeMode="contain" />
      </Header>
      <Separator />
      <KeyboardAvoidingView
        behavior={Platform.OS === 'ios' ? 'padding' : 'height'}
        style={{flex: 1}}>
        <ScrollView contentContainerStyle={{flexGrow: 1}} keyboardShouldPersistTaps="handled">
          <ContentContainer>
            <Title>Iniciar Sesión</Title>

            <InputCard>
              <InputLabel>Email</InputLabel>
              <InputWrapper>
                <StyledInput
                  placeholder="Ingresa tu email"
                  placeholderTextColor={theme.lightGreen}
                  value={email}
                  onChangeText={setEmail}
                  keyboardType="email-address"
                  autoCapitalize="none"
                  editable={!loading}
                />
                <InputIcon name="email-outline" size={24} color={theme.green} />
              </InputWrapper>
            </InputCard>

            <InputCard>
              <InputLabel>Contraseña</InputLabel>
              <InputWrapper>
                <StyledInput
                  placeholder="Ingresa tu contraseña"
                  placeholderTextColor={theme.lightGreen}
                  value={password}
                  onChangeText={setPassword}
                  secureTextEntry={!showPassword}
                  editable={!loading}
                />
                <TouchableOpacity onPress={() => setShowPassword(!showPassword)}>
                  <InputIcon name={showPassword ? 'eye-off-outline' : 'eye-outline'} size={24} color={theme.green} />
                </TouchableOpacity>
              </InputWrapper>
            </InputCard>

            <TouchableOpacity onPress={() => navigation.navigate('ForgotPassword')} disabled={loading}>
              <ForgotPasswordText>¿Olvidaste tu contraseña?</ForgotPasswordText>
            </TouchableOpacity>

            <AccessButton onPress={handleLogin} disabled={loading}>
              {loading ? (
                <ActivityIndicator color={theme.black} />
              ) : (
                <AccessButtonText>Acceder</AccessButtonText>
              )}
            </AccessButton>

            <TouchableOpacity onPress={() => Alert.alert('Contacto', 'Para crear una cuenta, por favor contacta con el administrador de tu organización.')} disabled={loading}>
              <FooterText>¿No tienes cuenta? Contacta con tu organización</FooterText>
            </TouchableOpacity>

            <BackButton onPress={() => navigation.goBack()} disabled={loading}>
              <BackButtonText>Volver</BackButtonText>
            </BackButton>
          </ContentContainer>
        </ScrollView>
      </KeyboardAvoidingView>
    </Container>
  );
};

export default LoginScreen;