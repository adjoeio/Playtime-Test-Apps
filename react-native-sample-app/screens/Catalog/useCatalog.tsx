import { useCallback, useEffect, useMemo, useState } from 'react';
import { PlaytimeStudio } from 'react-native-adjoe-sdk';
import { PlaytimeCampaign } from 'react-native-adjoe-sdk/src-ts/PlaytimeStudio';
import { USER_ID } from '../../util/constants';
import { Platform } from 'react-native';
import { requestTrackingPermission } from 'react-native-tracking-transparency';

export const useCatalog = () => {
    const [tosChecked, setTosChecked] = useState<boolean>();
    const [permissionsChecked, setPermissionsChecked] = useState<boolean>();
    const [activeTab, setActiveTab] = useState<'catalog' | 'installed'>('catalog');
    const [catalogCampaigns, setCatalogCampaigns] = useState<PlaytimeCampaign[]>([]);
    const [installedCampaigns, setInstalledCampaigns] = useState<PlaytimeCampaign[]>([]);
    const [clickedCampaign, setClickedCampaign] = useState<PlaytimeCampaign | null>(null);

    const loading = useMemo(() => { 
        return !(typeof tosChecked === 'boolean' && typeof permissionsChecked === 'boolean');
    }, [tosChecked, permissionsChecked]);

    const checkUsagePermission = useCallback(async () => {
        if (Platform.OS === 'ios') {
            const trackingStatus = await requestTrackingPermission();
            if (trackingStatus === 'authorized' || trackingStatus === 'unavailable') {
                setTosChecked(true); // AUTO TOS for studio
                setPermissionsChecked(true);
            } else {
                setTosChecked(true); // AUTO TOS for studio
                setPermissionsChecked(false);
            }
            return null;
        }

        try {
            const val = await PlaytimeStudio.getPermissions();
            setTosChecked(true); // AUTO TOS for studio
            setPermissionsChecked(val.permissions.isUsagePermissionAccepted);
        } catch (err) {
            console.error(err);
        }
    }, []);

    const fetchCampaigns = useCallback(async () => {
        if (tosChecked && permissionsChecked) {
            try {
                const apps = await PlaytimeStudio.getCampaigns({ userId: USER_ID });
                const installedApps = await PlaytimeStudio.getInstalledCampaigns({ userId: USER_ID });
                setCatalogCampaigns(apps.campaigns);
                setInstalledCampaigns(installedApps.campaigns);
            } catch (err) {
                console.error(err);
            }
        }
    }, [tosChecked, permissionsChecked]);

    useEffect(() => {
        fetchCampaigns();
    }, [fetchCampaigns]);

    useEffect(() => {
        checkUsagePermission();
    }, []);

    return {
        loading,
        activeTab,
        tosChecked,
        setActiveTab,
        setTosChecked,
        catalogCampaigns,
        installedCampaigns,
        permissionsChecked,
        setPermissionsChecked,
        clickedCampaign,
        setClickedCampaign,
    };
};
