import { ActivityIndicator, ScrollView, View } from 'react-native';
import { flexStyles, spacingStyles } from '../../util/style';
import { useCatalog } from './useCatalog';
import Onboarding from './components/Onboarding';
import Tabs from './components/Tabs';
import CatalogCampaign from './components/CatalogCampaign';
import InstalledCampaign from './components/InstalledCampaign';
import CampaignDetails from './components/CampaignDetails';
import SafeArea from '../../components/SafeArea';
import Button from '../../components/Button';
import { theme } from '../../util/theme';

export default function CatalogScreen() {
    const { 
        loading,
        activeTab,
        tosChecked,
        openChatbot,
        setActiveTab,
        setTosChecked,
        clickedCampaign,
        catalogCampaigns,
        permissionsChecked,
        installedCampaigns,
        setClickedCampaign,
        setPermissionsChecked,
        deeplinkInstalledApps,
     } = useCatalog();

    if (loading) {
        return (
            <View style={[flexStyles.flex1, flexStyles.justfiyCenter]}>
                <View 
                    style={[
                        flexStyles.flex1, 
                        flexStyles.justfiyCenter, 
                        flexStyles.row
                    ]}>
                    <ActivityIndicator size="large" />
                </View>
            </View>
        );
    }

    return (
        <SafeArea>
            <Onboarding 
                tosChecked={tosChecked}
                setTosChecked={setTosChecked}
                permissionsChecked={permissionsChecked}
                setPermissionsChecked={setPermissionsChecked}
            />

            <Tabs activeTab={activeTab} setActiveTab={setActiveTab} />

            <ScrollView style={[flexStyles.flex1]}>
                <View style={[spacingStyles.padding]}>
                    {activeTab === 'catalog' ? catalogCampaigns.map((campaign) => (
                        <CatalogCampaign 
                            setClickedCampaign={setClickedCampaign} 
                            campaign={campaign} 
                            key={campaign.campaignUUID}
                        />
                    )) : installedCampaigns.map((campaign) => (
                        <InstalledCampaign 
                            key={campaign.campaignUUID} 
                            campaign={campaign} 
                            setClickedCampaign={setClickedCampaign} 
                        />
                    ))}
                </View>
            </ScrollView>

            <CampaignDetails 
                clickedCampaign={clickedCampaign}
                setClickedCampaign={setClickedCampaign}
            />

            <View style={[spacingStyles.padding, { backgroundColor: theme.blueGrey }]}>
                <Button title="Need help with something? Ask our chatbot!" type="danger" onPress={openChatbot} />
                <Button title="Deeplink to Installed Campaign" type="link" onPress={deeplinkInstalledApps} />
            </View>
        </SafeArea>
    );
}
