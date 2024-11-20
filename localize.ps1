$t4Files = Get-ChildItem -Path . -Filter "*.tt" -Recurse
foreach ($file in $t4Files) {
	Write-Host "Processing $($file.FullName)"
	t4 $file.FullName
}