import { useCallback, useMemo } from "react";
import { Image, StyleSheet, Text, View, TouchableOpacity } from "react-native";
import { PlaytimeStudio, PlaytimeCampaign, PlaytimeCampaignStatus } from "react-native-adjoe-sdk";
import { flexStyles, spacingStyles, textStyles } from "../../../util/style";
import { getRewardedCoins, getTotalReward } from "../../../util/campaignsHelper";
import ProgressBar from "../../../components/ProgressBar";
import Button from "../../../components/Button";

type InstalledCampaignProps = {
    campaign: PlaytimeCampaign;
    setClickedCampaign: (campaign: PlaytimeCampaign) => void;
}

const styles = StyleSheet.create({
    shadowContainer: {
        shadowColor: 'rgba(0, 0, 0, 0.2)',
        elevation: 10,
    },
    container: {
        backgroundColor: 'white',
        borderRadius: 12,
    },
    image: {
        height: 40,
        width: 40,
        borderRadius: 12,
    },
    coinsContainer: {
        borderRadius: 12,
        borderWidth: 1,
        padding: 12,
    },
});

export default function InstalledCampaign({
    campaign,
    setClickedCampaign
}: InstalledCampaignProps) {
    const totalReward = useMemo(() => getTotalReward(campaign), [campaign]);
    const currentReward = useMemo(() => getRewardedCoins(campaign), [campaign]);

    const onPlayNowClicked = useCallback(async () => {
        try {
            await PlaytimeStudio.openInstalledCampaign(campaign);
        } catch (err) {
            console.error(err);
        }
    }, [campaign]);

    const deeplinkCampaign = useCallback(async () => {
        PlaytimeStudio.showCampaignDetails(campaign)
            .catch((err) => console.error(err));
    }, [campaign]);

    return (
        <TouchableOpacity
            activeOpacity={1}
            onPress={() => setClickedCampaign(campaign)}
            style={[spacingStyles.marginBottom, styles.shadowContainer]}
        >
            <View style={[styles.container]}>
                <View style={[spacingStyles.padding, flexStyles.row]}>
                    {campaign?.iconImage && (
                        <Image
                            style={[styles.image, spacingStyles.marginRight]}
                            source={{ uri: campaign?.iconImage || '' }}
                        />
                    )}

                    <View style={[flexStyles.flex1]}>
                        <Text style={[textStyles.textBold, spacingStyles.marginBottom]}>
                            {campaign.appName}
                        </Text>

                        <View style={[spacingStyles.marginBottom]}>
                            <View style={[flexStyles.row, flexStyles.justifyBetween]}>
                                <Text>
                                    {currentReward}
                                </Text>
                                <Text>
                                    {totalReward}
                                </Text>
                            </View>

                            <ProgressBar percentage={currentReward / totalReward * 100} />
                        </View>

                        {campaign.status === PlaytimeCampaignStatus.INSTALLED ? (
                            <>
                                <Button title="Play Now" onPress={onPlayNowClicked} />
                                <Button type="link" title="Deeplink to WebCatalog" onPress={deeplinkCampaign} />
                            </>
                        ) : campaign.status === PlaytimeCampaignStatus.FAILED ? (
                            <>
                                <Text style={[textStyles.textBold, spacingStyles.marginBottom]}>
                                    We can't reward you for this app. Feel free to continue using the app.
                                </Text>
                            </>
                        ) : campaign.status === PlaytimeCampaignStatus.PENDING ? (
                            <>
                                <Text style={[textStyles.textBold, spacingStyles.marginBottom]}>
                                    Veryfying installation...
                                </Text>
                            </>
                        ) : null}
                    </View>
                </View>
            </View>
        </TouchableOpacity>
    );
}
