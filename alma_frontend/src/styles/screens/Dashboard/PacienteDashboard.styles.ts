import styled from 'styled-components/native';
import {View, Text, TouchableOpacity, Image} from 'react-native';

export const theme = {
  primaryGreen: '#ACD467',
  lightGreen: '#E8F5E3',
  white: '#FFFFFF',
  black: '#000000',
  gray: '#888',
  red: '#e74c3c',
};

export const Container = styled(View)`
  flex: 1;
  background-color: ${theme.white};
`;

export const Header = styled(View)`
  align-items: center;
  padding: 16px;
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

export const Content = styled(View)`
    flex: 1;
    padding: 16px;
`;

export const ProgressCard = styled(View)`
    background-color: rgba(232, 245, 227, 0.5); /* semi-transparent light green */
    border-radius: 12px;
    padding: 24px;
    margin-bottom: 24px;
    align-items: center;
`;

export const ProgressText = styled(Text)`
    font-size: 16px;
    color: ${theme.black};
    text-align: center;
`;

export const Grid = styled(View)`
    flex-direction: row;
    flex-wrap: wrap;
    justify-content: space-around;
`;

export const GridButton = styled(TouchableOpacity)`
    width: 45%;
    aspect-ratio: 1;
    align-items: center;
    justify-content: center;
    margin-bottom: 16px;
`;

export const GridButtonInner = styled(View)`
    width: 100px;
    height: 100px;
    border-radius: 50px;
    background-color: ${theme.lightGreen};
    align-items: center;
    justify-content: center;
`;

export const GridButtonText = styled(Text)`
    font-size: 14px;
    color: ${theme.black};
    text-align: center;
    margin-top: 8px;
`;

export const BottomNav = styled(View)`
    flex-direction: row;
    justify-content: space-around;
    padding: 8px;
    border-top-width: 1px;
    border-top-color: #eee;
    background-color: ${theme.white};
`;

export const NavButton = styled(TouchableOpacity)`
    align-items: center;
`;

export const NavButtonText = styled(Text)`
    font-size: 12px;
    color: ${theme.gray};
`;

export const LogoutButton = styled(TouchableOpacity)`
    background-color: ${theme.red};
    padding: 16px;
    border-radius: 12px;
    margin: 0 16px 16px 16px;
    align-items: center;
`;

export const LogoutButtonText = styled(Text)`
    font-size: 18px;
    color: ${theme.white};
    font-weight: bold;
`;

export const ChangePasswordButton = styled(TouchableOpacity)`
    background-color: ${theme.primaryGreen};
    padding: 16px;
    border-radius: 12px;
    margin: 16px 16px 0 16px;
    align-items: center;
`;