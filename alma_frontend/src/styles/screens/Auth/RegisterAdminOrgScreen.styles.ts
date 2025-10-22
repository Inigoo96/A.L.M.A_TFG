import styled from 'styled-components/native';
import {Image, Text, TouchableOpacity, View, TextInput} from 'react-native';

export const theme = {
  primaryGreen: '#ACD467',
  lightGreen: '#ACD467',
  softGreenishGray: '#E0E0E0',
  black: '#000000',
  white: '#FFFFFF',
  green: '#ACD467',
  creamBackground: '#f1ebdd',
  grayText: '#888',
  disabledGray: '#F5F5F5',
};

export const Container = styled(View)`
  flex: 1;
  background-color: ${theme.white};
`;

export const Header = styled(View)`
  padding: 16px;
  align-items: flex-end;
`;

export const HeartIcon = styled(Image)`
  width: 40px;
  height: 40px;
`;

export const Title = styled(Text)`
  font-size: 28px;
  font-weight: bold;
  color: ${theme.black};
  text-align: center;
  margin-bottom: 24px;
`;

export const InputCard = styled(View)`
  background-color: ${theme.white};
  border-radius: 12px;
  border-width: 1px;
  border-color: ${theme.softGreenishGray};
  padding: 16px;
  margin-bottom: 16px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
`;

export const InputLabel = styled(Text)`
  font-size: 16px;
  color: ${theme.black};
  margin-bottom: 8px;
`;

export const InputWrapper = styled(View)`
  flex-direction: row;
  align-items: center;
  border-bottom-width: 1px;
  border-bottom-color: ${theme.softGreenishGray};
  padding-bottom: 8px;
`;

export const StyledInput = styled(TextInput)`
  flex: 1;
  font-size: 16px;
  color: ${theme.black};
  margin-left: 8px;
`;

export const DisabledInput = styled(StyledInput)`
    background-color: ${theme.disabledGray};
    color: ${theme.grayText};
`;

export const RegisterButton = styled(TouchableOpacity)`
  width: 100%;
  background-color: ${theme.primaryGreen};
  border-radius: 24px;
  padding: 16px;
  align-items: center;
  justify-content: center;
  margin-top: 16px;
  margin-bottom: 16px;
`;

export const ButtonText = styled(Text)`
  font-size: 20px;
  font-weight: bold;
  color: ${theme.black};
`;

export const BackButton = styled(TouchableOpacity)`
  padding: 8px;
  align-items: center;
  justify-content: center;
`;

export const BackButtonText = styled(Text)`
  font-size: 16px;
  color: ${theme.green};
`;

export const ErrorText = styled(Text)`
    font-size: 12px;
    color: #e74c3c;
    margin-top: 4px;
`;

// Password strength styles
export const PasswordStrengthContainer = styled(View)`
    margin-top: 8px;
`;

export const StrengthMeter = styled(View)`
    height: 5px;
    background-color: #E0E0E0;
    border-radius: 2.5px;
    margin-top: 4px;
    margin-bottom: 4px;
`;

export const StrengthIndicator = styled(View)`
    height: 100%;
    border-radius: 2.5px;
`;

export const RequirementText = styled(Text)`
    font-size: 12px;
    color: #888;
`;

export const RequirementMetText = styled(Text)`
    font-size: 12px;
    color: ${theme.green};
`;
