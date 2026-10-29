import { useCallback, useState } from "react";
import { PlaytimeStudio, TosConsentAction } from 'react-native-adjoe-sdk';
import { Platform, Text, View } from "react-native";
import Drawer from "../../../components/Drawer";
import { spacingStyles, textStyles } from "../../../util/style";
import Button from "../../../components/Button";
import { requestTrackingPermission } from "react-native-tracking-transparency";

type OnboardingProps = {
    tosChecked?: boolean;
    setTosChecked: (value: boolean) => void;
    permissionsChecked?: boolean;
    setPermissionsChecked: (value: boolean) => void;
};

export default function Onboarding({
    tosChecked,
    setTosChecked,
    permissionsChecked,
    setPermissionsChecked,
}: OnboardingProps) {
    const [modalVisible, setModalVisible] = useState(true);
    const [step, setStep] = useState(
        tosChecked ? (permissionsChecked ? 3 : 2) : 1
    );

    const incrementStep = useCallback(() => {
        if (permissionsChecked) {
            // Skip permissions screen
            setStep(3);
        } else {
            setStep((prevStep) => prevStep + 1);
        }
    }, []);

    const decrementStep = useCallback(() => {
        setStep((prevStep) => prevStep - 1);
    }, []);

    const onAcceptPermissions = useCallback(async () => {
        try {
            if (Platform.OS === 'ios') {
                await requestTrackingPermission();
            } else {
                await PlaytimeStudio.showPermissionsPrompt();
            }
            setTimeout(() => {
                setPermissionsChecked(true);
                incrementStep();
            }, 500);
        } catch (error) {
            console.error(error);
        }
    }, []);

    const onAcceptTos = useCallback(async () => {
        try {
            await PlaytimeStudio.updateTosConsent(TosConsentAction.ACCEPT);
            setTosChecked(true);
            incrementStep();
        } catch (error) {
            console.error(error);
        }
    }, []);

    if ((tosChecked && permissionsChecked) || step > 2) {
        return null; // If both are checked, do not render the modal
    }

    return (
        <Drawer
            visible={modalVisible}
            onRequestClose={() => setModalVisible(false)}
        >
            {step === 1 ? (
                <View>
                    <Text 
                        style={[
                            textStyles.textCenter, 
                            textStyles.textBold, 
                            spacingStyles.marginBottom,
                        ]}
                    >
                        Game, Play, Win
                    </Text>

                    <Text style={[textStyles.textCenter, spacingStyles.marginBottom]}>
                        Find a game you love, install it, and rack up rewards by completing levels.
                    </Text>

                    <View style={[spacingStyles.marginTop, spacingStyles.marginBottom]}>
                        <Button title="Accept TOS" onPress={onAcceptTos} />
                    </View>
                    <Button title="Skip" onPress={incrementStep} type="link" />
                </View>
            ) : step === 2 ? (
                <View>
                    <Text 
                        style={[
                            textStyles.textCenter, 
                            textStyles.textBold, 
                            spacingStyles.marginBottom,
                        ]}
                    >
                        Allow Tracking and Unlock Rewards
                    </Text>

                    <Text style={[textStyles.textCenter, spacingStyles.marginBottom]}>
                        To earn rewards we need permission to track your progress.
                        This helps us tailor your experience and deliver rewards.
                    </Text>

                    <View style={[spacingStyles.marginTop, spacingStyles.marginBottom]}>
                        <Button title="Agree and continue" onPress={onAcceptPermissions} />
                    </View>
                    <Button title="Back" onPress={decrementStep} type="link" />
                </View>
            ) : null}
            {null}
        </Drawer>
    )
}
