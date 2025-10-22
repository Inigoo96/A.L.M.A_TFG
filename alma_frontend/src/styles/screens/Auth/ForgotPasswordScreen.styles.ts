import styled from 'styled-components/native';
import {Image, Text, TouchableOpacity, View, TextInput} from 'react-native';

export const theme = {
  primaryGreen: '#ACD467',
  lightGreen: '#E8F5E3', // A softer green for background
  softGreenishGray: '#E0E0E0',
  black: '#000000',
  white: '#FFFFFF',
  green: '#ACD467',
};

export const Container = styled(View)`
  flex: 1;
  background-color: ${theme.white};
`;

export const Header = styled(View)`
  padding: 16px;
  align-items: flex-start;
`;

export const HeartIcon = styled(Image)`
  width: 40px;
  height: 40px;
`;

export const Separator = styled(View)`
  width: 100%;
  height: 1px;
  background-color: ${theme.primaryGreen};
`;

export const Title = styled(Text)`
  font-size: 28px;
  font-weight: bold;
  color: ${theme.black};
  text-align: center;
  margin-top: 24px;
  margin-bottom: 24px;
`;

export const Card = styled(View)`
  margin: 16px;
  padding: 24px;
  border-radius: 12px;
  border-width: 1px;
  border-color: ${theme.softGreenishGray};
  align-items: center;
`;

export const CardTitle = styled(Text)`
  font-size: 22px;
  font-weight: bold;
  color: ${theme.black};
  text-align: center;
  margin-bottom: 8px;
`;

export const CardSubtitle = styled(Text)`
  font-size: 16px;
  color: #666;
  text-align: center;
  margin-bottom: 24px;
`;

export const Illustration = styled(View)`
  margin-bottom: 24px;
`;

export const Form = styled(View)`
    width: 100%;
`;

export const InputLabel = styled(Text)`
  font-size: 18px;
  font-weight: bold;
  color: ${theme.black};
  margin-bottom: 8px;
`;

export const InputContainer = styled(View)`
  background-color: ${theme.lightGreen};
  border-radius: 12px;
  padding: 16px;
`;

export const StyledInput = styled(TextInput)`
  font-size: 16px;
  color: ${theme.black};
`;

export const SendButton = styled(TouchableOpacity)`
  width: 100%;
  background-color: ${theme.primaryGreen};
  border-radius: 24px;
  padding: 16px;
  align-items: center;
  justify-content: center;
  margin-top: 24px;
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
  margin-top: 16px;
`;

export const BackButtonText = styled(Text)`
  font-size: 16px;
  color: ${theme.green};
`;